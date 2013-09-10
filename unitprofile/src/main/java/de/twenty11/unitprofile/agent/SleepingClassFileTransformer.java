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
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class SleepingClassFileTransformer implements ClassFileTransformer {

    private final long offset = System.currentTimeMillis();

    private List<Invocation> invocations = new ArrayList<Invocation>();

    private List<CtMethod> profiledMethods = new ArrayList<CtMethod>();

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (!className.startsWith("de/")) {
            return classfileBuffer;
        }

        byte[] byteCode = classfileBuffer;
        ClassPool cp = ClassPool.getDefault();

        try {

            CtClass cc = cp.get(className.replace("/", "."));

            List<CtMethod> annotatedMethodsToProfile = findMethodsToProfile(cc);

            if (annotatedMethodsToProfile.size() > 0) {
                System.out.println("found " + annotatedMethodsToProfile.size() + " method(s) annotated for profiling.");
                System.out.println("");
            }
            for (CtMethod m : annotatedMethodsToProfile) {
                profile(m, 0);
            }
            byteCode = cc.toBytecode();
            cc.detach();
        } catch (Exception ex) {
            // TODO
            //ex.printStackTrace();
        }

        return byteCode;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("FINALLY" + invocations.size());
    }

    private void profile(final CtMethod m, final int depth) throws CannotCompileException {

        Invocation invocation = new Invocation();
        invocations.add(invocation);
        
        if (profiledMethods.contains(m)) {
            System.out.println("blocked");
            return;
        }
        profiledMethods.add(m);

        m.addLocalVariable("elapsedTime", CtClass.longType);
        //System.out.println(getBeforeStatements(m, depth));
        m.insertBefore(getBeforeStatements(m, depth));
        m.insertAfter(getAfterStatements(m, depth));

        m.instrument(new ExprEditor() {
            public void edit(MethodCall mc) throws CannotCompileException {
                if (mc.getClassName().startsWith("java.")) {
                    return;
                }
                if (mc.getClassName().startsWith("de.twenty11.unitprofile.callback.")) {
                    return;
                }
               // System.out.println(m.getName() + " -> " + mc.getClassName() + "." + mc.getMethodName() + " " + mc.getSignature());
                try {
                    int newDepth = depth + 1;
                    profile(mc.getMethod(), newDepth);
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        });
    }

    private String getBeforeStatements(CtMethod m, int depth) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("elapsedTime = System.currentTimeMillis(); ");
        sb.append("de.twenty11.unitprofile.callback.ProfilerCallback.before(this.getClass().getName(), \""+m.getName()+"\", "+depth+", elapsedTime);");
//        sb.append("System.out.println(\"Before " + m.getName() + "[" + m.hashCode() + "] (" + depth
//                + ")(\" + (elapsedTime - " + offset + "L) +\")\");");
        sb.append("}");
        return sb.toString();
    }

    private String getAfterStatements(CtMethod m, int depth) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("elapsedTime = System.currentTimeMillis();");
        sb.append("de.twenty11.unitprofile.callback.ProfilerCallback.after(this.getClass().getName(), \""+m.getName()+"\", "+depth+", elapsedTime);");
//        sb.append("System.out.println(\"After  " + m.getName() + "[" + m.hashCode() + "] (" + depth
//                + ")(\" + (elapsedTime - " + offset + "L) +\")\");");
        sb.append("}");
        return sb.toString();
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
                    // System.out.println(" >" + annotations[j].toString());
                    if (annotations[j].toString().equals("@de.twenty11.unitprofile.annotations.Profile")) {
                        methodsToProfile.add(declaredMethods[i]);
                    }
                }
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return methodsToProfile;
    }
}
