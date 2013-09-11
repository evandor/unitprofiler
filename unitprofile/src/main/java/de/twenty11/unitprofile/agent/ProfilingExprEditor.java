package de.twenty11.unitprofile.agent;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ProfilingExprEditor extends ExprEditor {
    
    private int depth;
    private ProfilingClassFileTransformer fileTransformer;

    public ProfilingExprEditor(ProfilingClassFileTransformer fileTransformer, int depth) {
        this.fileTransformer = fileTransformer;
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
            fileTransformer.profile(mc.getMethod(), depth + 1);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
}
