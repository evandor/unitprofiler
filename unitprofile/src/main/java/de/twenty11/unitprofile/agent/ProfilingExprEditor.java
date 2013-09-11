package de.twenty11.unitprofile.agent;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ProfilingExprEditor extends ExprEditor {
    
    private int depth;
    private ProfilingClassFileTransformer fileTransformer;
    private CtClass cc;

    public ProfilingExprEditor(ProfilingClassFileTransformer fileTransformer, CtClass cc, int depth) {
        this.fileTransformer = fileTransformer;
        this.cc = cc;
        this.depth = depth;
    }
    
    public void edit(MethodCall mc) throws CannotCompileException {
        if (mc.getClassName().startsWith("java.")) {
            return;
        }
        if (mc.getClassName().startsWith("de.twenty11.unitprofile.callback.")) {
            return;
        }
        try {
            fileTransformer.profile(mc.getMethod(), cc, depth + 1);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
}
