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
                logger.info("found " + annotatedMethodsToProfile.size() + " method(s) annotated for profiling: ");
                logger.info(annotatedMethodsToProfile.toString());
                logger.info("");
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

    private final void startProfiling(CtClass classWithProfilingAnnotatedMethod, CtClass profilerClass, final CtMethod m)
            throws CannotCompileException {

        if (!instrument(m)) {
            return;
        }

        classWithProfilingAnnotatedMethod.instrument(new ProfilingExprEditor(this, classWithProfilingAnnotatedMethod));

        m.insertBefore("{ProfilerCallback.start(this.getClass().getName(), \"" + m.getName() + "\");}");
        m.insertAfter("{ProfilerCallback.stop(this.getClass().getName(), \"" + m.getName() + "\");}");
        m.instrument(new ProfilingExprEditor(this, classWithProfilingAnnotatedMethod));
    }

    protected final void profile(final CtMethod m, CtClass cc) throws CannotCompileException {

        if (!instrument(m)) {
            return;
        }

        m.insertBefore("{ProfilerCallback.before(this.getClass().getName(), \"" + m.getName() + "\");}");
        m.insertAfter("{ProfilerCallback.after(this.getClass().getName(), \"" + m.getName() + "\");}");
        m.instrument(new ProfilingExprEditor(this, cc));
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
        Instrumentation instrumentation = new Instrumentation(method.getDeclaringClass().getName(), method.getName());
        if (instrumentations.contains(instrumentation)) {
            return false;
        }
        instrumentations.add(instrumentation);
        return true;
    }
    
}
