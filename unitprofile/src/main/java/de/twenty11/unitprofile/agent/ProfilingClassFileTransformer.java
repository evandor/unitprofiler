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
import de.twenty11.unitprofile.Profiler;
import de.twenty11.unitprofile.callback.ProfilerCallback;

public class ProfilingClassFileTransformer implements ClassFileTransformer {

    private List<CtMethod> profiledMethods = new ArrayList<CtMethod>();

    private CtClass profilerCallbackCtClass;

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (!className.startsWith("de/")) {
            return classfileBuffer;
        }

        byte[] byteCode = classfileBuffer;
        ClassPool cp = ClassPool.getDefault();
        //cp.importPackage("de.twenty11.unitprofile");
        cp.importPackage("de.twenty11.unitprofile.callback");
        
        try {

            CtClass cc = cp.get(className.replace("/", "."));

            if (profilerCallbackCtClass == null) {
                profilerCallbackCtClass = cp.get(ProfilerCallback.class.getName());
            }
            
            List<CtMethod> annotatedMethodsToProfile = findMethodsToProfile(cc);

            if (annotatedMethodsToProfile.size() > 0) {
                System.out.println("found " + annotatedMethodsToProfile.size() + " method(s) annotated for profiling.");
                System.out.println("");
            }
            for (CtMethod m : annotatedMethodsToProfile) {
                startProfiling(cc, profilerCallbackCtClass, m);
            }
            byteCode = cc.toBytecode();
            cc.detach();
        } catch (Exception ex) {
            // TODO
            //ex.printStackTrace();
        }

        return byteCode;
    }

    private final void startProfiling(CtClass cc, CtClass profilerClass, final CtMethod m) throws CannotCompileException {
        
        if (profiledMethods.contains(m)) {
            return;
        }
        profiledMethods.add(m);

        CtField f = new CtField(profilerClass, "profiler", cc);
        cc.addField(f);
        
        m.insertBefore("{ProfilerCallback.start(this.getClass().getName(), \""+m.getName()+"\");}");
        m.insertAfter("{ProfilerCallback.stop(this.getClass().getName(), \""+m.getName()+"\");}");
        m.instrument(new ProfilingExprEditor(this, cc, 0));
    }


    protected final void profile(final CtMethod m, CtClass cc, final int depth) throws CannotCompileException {
         
        if (profiledMethods.contains(m)) {
            return;
        }
        profiledMethods.add(m);

        m.insertBefore("{ProfilerCallback.before(this.getClass().getName(), \""+m.getName()+"\");}");
        m.insertAfter("{ProfilerCallback.after(this.getClass().getName(), \""+m.getName()+"\");}");
        m.instrument(new ProfilingExprEditor(this, cc, depth));
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
