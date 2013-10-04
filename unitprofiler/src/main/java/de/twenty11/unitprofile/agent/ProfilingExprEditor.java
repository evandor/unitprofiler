package de.twenty11.unitprofile.agent;

import java.lang.instrument.ClassDefinition;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.twenty11.unitprofile.domain.MethodDescriptor;
import de.twenty11.unitprofile.domain.Transformation;

public class ProfilingExprEditor extends ExprEditor {

    private static final Logger logger = LoggerFactory.getLogger(ProfilingExprEditor.class);

    private ProfilingClassFileTransformer classTransformer;
    private CtClass cc;

    public ProfilingExprEditor(ProfilingClassFileTransformer fileTransformer, CtClass cc) {
        this.classTransformer = fileTransformer;
        this.cc = cc;
    }

    public void edit(MethodCall mc) throws CannotCompileException {
        logger.info("MethodCall {}", mc.getClassName() + "#" + mc.getMethodName() + "(line "+mc.getLineNumber()+")");
        if (excluded(mc)) {
            return;
        }
        try {
            classTransformer.profile(mc.getMethod(), cc);
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
        logger.warn("ConstructorCall {}", c.getClassName() + "#" + c.getMethodName() + "(line " + c.getLineNumber()
                + ")");
        logger.warn("");
    }

    @Override
    public void edit(FieldAccess f) throws CannotCompileException {
        logger.warn("fieldAccess {}", f);
        // logger.warn("");
    }

    @Override
    public void edit(Handler h) throws CannotCompileException {
        logger.warn("handler {}", h);
        // logger.warn("");
    }

    @Override
    public void edit(NewArray newArray) throws CannotCompileException {
        logger.warn("NewArray {} line {}", newArray.getFileName(), newArray.getLineNumber());
        try {
            logger.warn("NewArray componentType {}", newArray.getComponentType());
        } catch (NotFoundException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        }
        logger.warn("NewArray, created dim. {}, dim {}", newArray.getCreatedDimensions(), newArray.getDimension());
        logger.warn("");

    }

    @Override
    public void edit(NewExpr newExpression) throws CannotCompileException {
        logger.warn("NewExpr {} line {}", newExpression.getFileName(), newExpression.getLineNumber());
        try {
            CtConstructor constructor = newExpression.getConstructor();
            CtClass ctClass = constructor.getDeclaringClass();
            MethodDescriptor methodDescriptor = new MethodDescriptor(newExpression);

            if (classTransformer.isAlreadyInstrumented(methodDescriptor)) {
                return;
            }
            classTransformer.addInstrumentation(methodDescriptor);

            if (ctClass.isFrozen()) {
                logger.warn("'{}' is 'frozen'", ctClass.getName());
                return;
            }

            instrument(constructor, ctClass, methodDescriptor);

            Transformation transformation = classTransformer.getTransformation(ctClass.getName());
            if (transformation != null) {
                java.lang.instrument.Instrumentation javainstrumentation = classTransformer.getInstrumentation();
                ProfilingClassFileTransformer localTransformer = new ProfilingClassFileTransformer(javainstrumentation);
                javainstrumentation.addTransformer(localTransformer, true);
                Class<?> cls1 = Class.forName(ctClass.getName());
                ClassDefinition classDefinition = new ClassDefinition(cls1, ctClass.toBytecode());
                javainstrumentation.redefineClasses(classDefinition);
                
                javainstrumentation.removeTransformer(localTransformer);
                
            }
        } catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
        }

    }

    private void instrument(CtConstructor constructor, CtClass ctClass, MethodDescriptor methodDescriptor)
            throws CannotCompileException {
        constructor.insertBeforeBody(methodDescriptor.getBeforeBody());
        constructor.insertAfter(methodDescriptor.getAfter());
        constructor.instrument(new ProfilingExprEditor(classTransformer, ctClass));
    }
}
