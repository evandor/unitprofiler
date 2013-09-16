package de.twenty11.unitprofile.agent;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.Handler;
import javassist.expr.MethodCall;
import javassist.expr.NewArray;
import javassist.expr.NewExpr;
import de.twenty11.unitprofile.domain.Instrumentation;

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
        //System.out.println(mc.getClassName() + "#" + mc.getMethodName());
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
        
    }
    
    @Override
    public void edit(FieldAccess f) throws CannotCompileException {
//        System.out.println("FieldAccess  " + f.getClassName());
//        System.out.println("FieldAccess  " + f.getFieldName());
//        System.out.println("FieldAccess  " + f.getEnclosingClass().getName());
//        System.out.println("FieldAccess  " + f.getLineNumber());
//        System.out.println("FieldAccess  " + f.where());
//        System.out.println("FieldAccess  " + f.getSignature());
//        System.out.println("FieldAccess  " + f.toString());
//        
//        
//        CtField field;
//        try {
//            field = f.getField();
//            System.out.println("FieldAccess  " + field.getName());
//            
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        }
//        System.out.println("");
    }
    
    @Override
    public void edit(Handler h) throws CannotCompileException {
        System.out.println("Handler  " + h.getFileName());
        System.out.println("Handler  " + h.getLineNumber());
        System.out.println("");
    }
    
    @Override
    public void edit(NewArray a) throws CannotCompileException {
        System.out.println("NewArray " + a);
    }
    
    @Override
    public void edit(NewExpr e) throws CannotCompileException {
        try {
            CtConstructor constructor = e.getConstructor();
            Instrumentation instrumentation = new Instrumentation(e.getClassName(), e.getConstructor().getName());
            
            if (fileTransformer.alreadyInstrumented(instrumentation)) {
                return;
            }
            fileTransformer.addInstrumentation(instrumentation);
            
            //System.out.println("New Constructor: " + constructor.getLongName() +":"+ constructor.toString());
            
            constructor.insertBeforeBody("{ProfilerCallback.before(this.getClass().getName(), \""+constructor.getName()+"\");}");
            constructor.insertAfter("{ProfilerCallback.after(this.getClass().getName(), \""+constructor.getName()+"\");}");
            //constructor.instrument(new ProfilingExprEditor(fileTransformer, cc, depth));
        
        } catch (NotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        
    }
}
