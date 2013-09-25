package de.twenty11.unitprofile.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.CodeAttribute;
import de.twenty11.unitprofile.callback.ProfilerCallback;
import de.twenty11.unitprofile.domain.Instrumentation;
import de.twenty11.unitprofile.domain.Invocation;

/**
 * finds (for profiling) annotated methods and uses them as root for instrumentation.
 * 
 */
public class ProfilingClassFileTransformer implements ClassFileTransformer {

    private static final Logger logger = LoggerFactory.getLogger(ProfilingClassFileTransformer.class);

    /**
     * list of all methods (identified by treadName/objectName/methodName) which have been instrumented.
     */
    private List<Instrumentation> instrumentations = new ArrayList<Instrumentation>();

    private CtClass profilerCallbackCtClass;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (!className.startsWith("de/")) {
            return classfileBuffer;
        }

        byte[] byteCode = classfileBuffer;
        ClassPool cp = ClassPool.getDefault();
        cp.importPackage("de.twenty11.unitprofile.callback");

        try {

            CtClass cc = cp.get(className.replace("/", "."));

            if (profilerCallbackCtClass == null) {
                profilerCallbackCtClass = cp.get(ProfilerCallback.class.getName());
            }

            List<CtMethod> annotatedMethodsToProfile = findMethodsToProfile(cc);

            if (annotatedMethodsToProfile.size() > 0) {
                logInfoAboutAnnotatedMethodsFound(annotatedMethodsToProfile);
            }
            for (CtMethod m : annotatedMethodsToProfile) {
                startProfiling(cc, profilerCallbackCtClass, m);
            }
            byteCode = cc.toBytecode();
            cc.detach();
        } catch (Exception ex) {
            // TODO
            // ex.printStackTrace();
        }

        return byteCode;
    }

    private void logInfoAboutAnnotatedMethodsFound(List<CtMethod> annotatedMethodsToProfile) {
        logger.info("found " + annotatedMethodsToProfile.size() + " method(s) annotated for profiling: ");
        for (CtMethod ctMethod : annotatedMethodsToProfile) {
            logger.info(" * {}", ctMethod.getLongName());
        }
        logger.info("");
    }

    private final void startProfiling(CtClass classWithProfilingAnnotatedMethod, CtClass profilerClass, final CtMethod m)
            throws CannotCompileException {

        if (!instrument(m)) {
            return;
        }

        classWithProfilingAnnotatedMethod.instrument(new ProfilingExprEditor(this, classWithProfilingAnnotatedMethod));

        int lineNumber = m.getMethodInfo().getLineNumber(0);
        if (!Modifier.isStatic(m.getModifiers())) {
            m.insertBefore("{ProfilerCallback.start(this.getClass().getName(), \"" + m.getName() + "\", "+lineNumber+");}");
            m.insertAfter("{ProfilerCallback.stop(this.getClass().getName(), \"" + m.getName() + "\");}");
        } else {
            m.insertBefore("{ProfilerCallback.start(\"" + m.getDeclaringClass().getName() + "\", \"" + m.getName()
                    + "\");}");
            m.insertAfter("{ProfilerCallback.stop(\"" + m.getDeclaringClass().getName() + "\", \"" + m.getName()
                    + "\", \"+lineNumber+\");}");
        }
        m.instrument(new ProfilingExprEditor(this, classWithProfilingAnnotatedMethod));
    }

    protected final void profile(final CtMethod m, CtClass cc) throws CannotCompileException {

        if (!instrument(m)) {
            return;
        }

        int lineNumber = m.getMethodInfo().getLineNumber(0);

        logger.debug("profiling {}#{} ({})", new Object[]{cc.getName(),m.getName(),lineNumber});

        if (!Modifier.isStatic(m.getModifiers())) {
            m.insertBefore("{ProfilerCallback.before(this.getClass().getName(), \"" + m.getName() + "\", "+lineNumber+");}");
            m.insertAfter("{ProfilerCallback.after(this.getClass().getName(), \"" + m.getName() + "\");}");
            m.instrument(new ProfilingExprEditor(this, cc));
        } else {
            m.insertBefore("{ProfilerCallback.before(\"" + m.getDeclaringClass().getName() + "\", \"" + m.getName()
                    + "\", "+lineNumber+");}");
            m.insertAfter("{ProfilerCallback.after(\"" + m.getDeclaringClass().getName() + "\", \"" + m.getName()
                    + "\");}");
            m.instrument(new ProfilingExprEditor(this, cc));
        }
    }

    private List<CtMethod> findMethodsToProfile(CtClass cc) {

        List<CtMethod> methodsToProfile = new ArrayList<CtMethod>();

        CtMethod[] declaredMethods = cc.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            // System.out.println(declaredMethods[i].toString());
            Object[] annotations;
            try {
                annotations = declaredMethods[i].getAnnotations();
                if (annotations == null) {
                    continue;
                }
                for (int j = 0; j < annotations.length; j++) {
                    if (annotations[j].toString().equals("@de.twenty11.unitprofile.annotations.Profile")) {
                        methodsToProfile.add(declaredMethods[i]);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        return methodsToProfile;
    }

    public boolean alreadyInstrumented(Instrumentation instrumentation) {
        return instrumentations.contains(instrumentation);
    }

    public void addInstrumentation(Instrumentation instrumentation) {
        instrumentations.add(instrumentation);
    }

    public List<Instrumentation> getInstrumentations() {
        return instrumentations;
    }

    private boolean instrument(CtMethod method) {
        String objectName = method.getDeclaringClass().getName();
        Instrumentation instrumentation = new Instrumentation(objectName, method.getName());
        if (instrumentations.contains(instrumentation)) {
            return false;
        }
        logger.debug("added to instrumentations: " + objectName + "#" + method.getName() + "(line " + method.getMethodInfo().getLineNumber(0) + ")");
        instrumentations.add(instrumentation);
        
        CodeAttribute ca = method.getMethodInfo().getCodeAttribute();
        if (ca == null) {
            return false;
        }

        return true;
    }

}
