package de.twenty11.unitprofile.agent;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.Handler;
import javassist.expr.MethodCall;
import javassist.expr.NewArray;
import javassist.expr.NewExpr;

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
    
    @Override
    public void edit(ConstructorCall c) throws CannotCompileException {
        System.out.println("Constructor call " + c);
    }
    
    @Override
    public void edit(FieldAccess f) throws CannotCompileException {
        System.out.println("FieldAccess  " + f);
    }
    
    @Override
    public void edit(Handler h) throws CannotCompileException {
        System.out.println("Handler " + h);
    }
    
    @Override
    public void edit(NewArray a) throws CannotCompileException {
        System.out.println("NewArray " + a);
    }
    
    @Override
    public void edit(NewExpr e) throws CannotCompileException {
        System.out.println("NewExpr " + e);
    }
}
