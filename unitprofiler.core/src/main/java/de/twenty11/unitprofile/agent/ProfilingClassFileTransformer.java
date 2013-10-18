package de.twenty11.unitprofile.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.twenty11.unitprofile.callback.ProfilerCallback;
import de.twenty11.unitprofile.domain.MethodDescriptor;
import de.twenty11.unitprofile.domain.Transformation;
import de.twenty11.unitprofiler.annotations.Profile;

/**
 * finds (for profiling) annotated methods and uses them as root for instrumentation.
 * 
 */
public class ProfilingClassFileTransformer implements ClassFileTransformer {

    private static final String PROFILE_ANNOTATION = "@" + Profile.class.getName();

    private static final Logger logger = LoggerFactory.getLogger(ProfilingClassFileTransformer.class);

    /**
     * list of all classes which have been transformed
     */
    private List<Transformation> transformations = new ArrayList<Transformation>();

    /**
     * list of all methods (identified by objectName/methodName) which have been instrumented.
     * 
     * http://docs.oracle.com/javase/6/docs/api/java/lang/instrument/Instrumentation.html:
     * "Instrumentation is the addition of byte-codes to methods for the purpose of gathering data to be utilized by tools"
     */
    private List<MethodDescriptor> instrumentations = new ArrayList<MethodDescriptor>();

    private CtClass profilerCallbackCtClass;

    private java.lang.instrument.Instrumentation instrumentation;

    public ProfilingClassFileTransformer(java.lang.instrument.Instrumentation inst) {
        this.instrumentation = inst;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (shouldNotBeProfiled(className)) {
            return classfileBuffer;
        }

        Transformation transformation = trackTransformations(className, classfileBuffer);

        byte[] byteCode = classfileBuffer;
        ClassPool classPool = ClassPool.getDefault();

        classPool.importPackage("de.twenty11.unitprofile.callback");

        try {

            CtClass ctClass = classPool.get(className.replace("/", "."));

            if (profilerCallbackCtClass == null) {
                profilerCallbackCtClass = classPool.get(ProfilerCallback.class.getName());
            }

            List<CtMethod> annotatedMethodsToProfile = findMethodsToProfile(ctClass);

            if (annotatedMethodsToProfile.size() > 0) {
                logInfoAboutAnnotatedMethodsFound(annotatedMethodsToProfile);
            }
            for (CtMethod m : annotatedMethodsToProfile) {
                startProfiling(ctClass, profilerCallbackCtClass, m);
            }
            for (CtMethod m : ctClass.getMethods()) {
                profile(m, ctClass);
            }
            byteCode = ctClass.toBytecode();
            transformation.update(byteCode.length);
            // logger.debug("transformation updated '{}'", transformation);
            ctClass.detach();
        } catch (NotFoundException nfe) {
            logger.warn("{}", nfe.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return byteCode;
    }

    private boolean shouldNotBeProfiled(String className) {
        if (className.startsWith("java/") || className.startsWith("javax/") || className.startsWith("sun/")) {
            return true;
        }
        if (className.startsWith("org/junit") || className.startsWith("junit/framework")) {
            return true;
        }
        if (className.startsWith("de/twenty11/unitprofile/agent")) {
            return true;
        }
        if (className.startsWith("de/twenty11/unitprofile/domain")) {
            return true;
        }
        if (className.startsWith("de/twenty11/unitprofile/callback")) {
            return true;
        }
        if (className.startsWith("org/apache/maven/surefire")) {
            return true;
        }
        if (className.startsWith("org/apache/commons")) {
            return true;
        }
        if (className.startsWith("org/springframework")) {
            return true;
        }
        if (className.startsWith("org/jacoco/agent")) {
            return true;
        }
        return false;
    }

    private Transformation trackTransformations(String className, byte[] classfileBuffer) {
        Transformation transformation = new Transformation(className, classfileBuffer.length);
        if (transformations.contains(transformation)) {
            logger.warn("re-transforming '{}'", transformation);
        } else {
            transformations.add(transformation);
        }
        return transformation;
    }

    private void logInfoAboutAnnotatedMethodsFound(List<CtMethod> annotatedMethodsToProfile) {
        logger.info("found " + annotatedMethodsToProfile.size() + " method(s) annotated for profiling: ");
        for (CtMethod ctMethod : annotatedMethodsToProfile) {
            logger.info(" * {}", ctMethod.getDeclaringClass().getName() + "#" + ctMethod.getName());
        }
        logger.info("");
    }

    private final void startProfiling(CtClass classWithProfilingAnnotatedMethod, CtClass profilerClass, final CtMethod m)
            throws Exception {

        if (!instrument(m)) {
            return;
        }

        int lineNumber = m.getMethodInfo().getLineNumber(0);
        String code = "{ProfilerCallback.start(\"" + m.getDeclaringClass().getName() + "\", \"" + m.getName() + "\", "
                + lineNumber + ");}";
        logger.info("insertBefore: '{}'", code);
        m.insertBefore(code);
        m.insertAfter("{ProfilerCallback.stop(\"" + m.getDeclaringClass().getName() + "\", \"" + m.getName() + "\");}");
        m.instrument(new ProfilingExprEditor(this, classWithProfilingAnnotatedMethod));

        classWithProfilingAnnotatedMethod.instrument(new ProfilingExprEditor(this, classWithProfilingAnnotatedMethod));

    }

    protected final void profile(final CtMethod m, CtClass cc) throws CannotCompileException {

        if (!instrument(m)) {
            return;
        }

        MethodDescriptor md = new MethodDescriptor(m.getDeclaringClass().getName(), m.getName(), m.getMethodInfo()
                .getLineNumber(0));
        String insertBeforeCode = md.getInsertBefore();
        // logger.debug(insertBeforeCode);

        m.insertBefore(insertBeforeCode);
        m.insertAfter(md.getInsertAfter());
        // m.instrument(new ProfilingExprEditor(this, cc));
    }

    public boolean isAlreadyInstrumented(MethodDescriptor instrumentation) {
        return instrumentations.contains(instrumentation);
    }

    public void addInstrumentation(MethodDescriptor instrumentation) {
        instrumentations.add(instrumentation);
    }

    public List<MethodDescriptor> getInstrumentations() {
        return instrumentations;
    }

    public java.lang.instrument.Instrumentation getInstrumentation() {
        return this.instrumentation;
    }

    public Transformation getTransformation(String classname) {
        for (Transformation transformation : transformations) {
            if (transformation.getClassName().equals(classname)) {
                return transformation;
            }
        }
        return null;
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
                    if (annotations[j].toString().equals(PROFILE_ANNOTATION)) {
                        methodsToProfile.add(declaredMethods[i]);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        return methodsToProfile;
    }

    private boolean instrument(CtMethod method) {

        if (method.getDeclaringClass().isFrozen()) {
            logger.warn("'{}' is 'frozen'", method.getDeclaringClass().getName());
            return false;
        }

        String objectName = method.getDeclaringClass().getName();
        MethodDescriptor instrumentation = new MethodDescriptor(objectName, method.getName(), method.getMethodInfo()
                .getLineNumber(0));
        if (instrumentations.contains(instrumentation)) {
            return false;
        }
        // logger.debug("added to instrumentations: " + objectName + "#" + method.getName() + "(line "
        // + method.getMethodInfo().getLineNumber(0) + ")");
        instrumentations.add(instrumentation);

        CodeAttribute ca = method.getMethodInfo().getCodeAttribute();
        if (ca == null) {
            return false;
        }

        return true;
    }

}
