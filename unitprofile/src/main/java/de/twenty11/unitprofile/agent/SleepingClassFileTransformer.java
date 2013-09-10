package de.twenty11.unitprofile.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class SleepingClassFileTransformer implements ClassFileTransformer {

    private final long offset = System.currentTimeMillis();
    
    private List<Invocation> invocations = new ArrayList<Invocation>();
    
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        byte[] byteCode = classfileBuffer;
        ClassPool cp = ClassPool.getDefault();

        if (className.equals("artifact/InstrumentationTest")) {
            try {

                CtClass cc = cp.get("artifact.InstrumentationTest");

                // CtMethod[] declaredMethods = cc.getDeclaredMethods();

                List<CtMethod> methodsToProfile = findMethodsToProfile(cc);

                System.out.println("found " + methodsToProfile.size() + " method(s) annotated for profiling.");
                //System.out.println("Before somethingElse(0)(" + (offset - 1378736349658L) +")");
                for (CtMethod m : methodsToProfile) {
                    // CtMethod m = cc.getDeclaredMethod("randomSleep");
                    profile(m, 0);
                }
                byteCode = cc.toBytecode();
                cc.detach();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        // String name = className.replace("/", ".");

        return byteCode;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("FINALLY" + invocations.size());
    }
    
    
    private void profile(CtMethod m, final int depth) throws CannotCompileException {
        
        Invocation invocation = new Invocation();
        invocations.add(invocation);
               
        m.addLocalVariable("elapsedTime", CtClass.longType);
        //System.out.println(getBeforeStatements(m, depth));
        
        m.insertBefore(getBeforeStatements(m, depth));
        m.insertAfter(getAfterStatements(m, depth));
        
        m.instrument(new ExprEditor() {
            public void edit(MethodCall mc) throws CannotCompileException {
                if (mc.getClassName().startsWith("java.")) {
                    return;
                }
                //System.out.println("called: " + mc.getClassName() + "." + mc.getMethodName() + " " + mc.getSignature());
                try {
                    int newDepth = depth+1;
                    profile(mc.getMethod(), newDepth);
                } catch (Exception e) {
                   // e.printStackTrace();
                }
            }
        });
    }

    private String getBeforeStatements(CtMethod m, int depth) {
        StringBuilder sb = new StringBuilder ("{");
        sb.append("elapsedTime = System.currentTimeMillis();");
        sb.append("System.out.println(\"Before " + m.getName() + "["+m.hashCode()+"] ("+depth+")(\" + (elapsedTime - " + offset + "L) +\")\");");
        sb.append("}");
        return sb.toString();
    }

    private String getAfterStatements(CtMethod m, int depth) {
        StringBuilder sb = new StringBuilder ("{");
        //sb.append("elapsedTime = System.currentTimeMillis() - elapsedTime;");
        sb.append("elapsedTime = System.currentTimeMillis();");
        sb.append("System.out.println(\"After  " + m.getName() + "["+m.hashCode()+"] ("+depth+")(\" + (elapsedTime - " + offset + "L) +\")\");");
        sb.append("}");
        return sb.toString();
    }

    private List<CtMethod> findMethodsToProfile(CtClass cc) {

        List<CtMethod> methodsToProfile = new ArrayList<CtMethod>();

        CtMethod[] declaredMethods = cc.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            //System.out.println(declaredMethods[i].toString());
            Object[] annotations;
            try {
                annotations = declaredMethods[i].getAnnotations();
                if (annotations == null) {
                    continue;
                }
                for (int j = 0; j < annotations.length; j++) {
                    //System.out.println(" >" + annotations[j].toString());
                    if (annotations[j].toString().equals("@artifact.Profile")) {
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
