package de.twenty11.unitprofile.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private static final Logger logger = LoggerFactory.getLogger(ProfilingExprEditor.class);
    
    private ProfilingClassFileTransformer fileTransformer;
    private CtClass cc;

    public ProfilingExprEditor(ProfilingClassFileTransformer fileTransformer, CtClass cc) {
        this.fileTransformer = fileTransformer;
        this.cc = cc;
    }
    
    public void edit(MethodCall mc) throws CannotCompileException {
        //logger.debug(mc.getClassName() + "#" + mc.getMethodName());
        if (excluded(mc)) {
            return;
        }
        try {
            fileTransformer.profile(mc.getMethod(), cc);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    private boolean excluded(MethodCall mc) {
        if (mc.getClassName().startsWith("java.")) {
            return true;
        }
        if (mc.getClassName().startsWith("de.twenty11.unitprofile.callback.")) {
            return true;
        }
        if (mc.getClassName().startsWith("de.twenty11.unitprofile.domain.")) {
            return true;
        }
        return false;
    }

    @Override
    public void edit(ConstructorCall c) throws CannotCompileException {
        
    }
    
    @Override
    public void edit(FieldAccess f) throws CannotCompileException {
        System.out.println("FieldAccess  " + f.getClassName());
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
//        System.out.println("Handler  " + h.getLineNumber());
//        System.out.println("");
    }
    
    @Override
    public void edit(NewArray a) throws CannotCompileException {
        logger.info("NewArray {}", a.getFileName());
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
            logger.error(e1.getMessage(), e1);
        }
        
        
    }
}
