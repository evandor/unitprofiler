package de.twenty11.unitprofile.agent;

import java.lang.instrument.ClassDefinition;

import javassist.CannotCompileException;
import javassist.CtBehavior;
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

import de.twenty11.unitprofile.domain.Instrumentation;
import de.twenty11.unitprofile.domain.Transformation;

public class ProfilingExprEditor extends ExprEditor {

    private static final Logger logger = LoggerFactory.getLogger(ProfilingExprEditor.class);

    private ProfilingClassFileTransformer fileTransformer;
    private CtClass cc;

    public ProfilingExprEditor(ProfilingClassFileTransformer fileTransformer, CtClass cc) {
        this.fileTransformer = fileTransformer;
        this.cc = cc;
    }

    public void edit(MethodCall mc) throws CannotCompileException {
        // logger.info("MethodCall {}", mc.getClassName() + "#" + mc.getMethodName() + "(line "+mc.getLineNumber()+")");
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
        logger.warn("ConstructorCall {}", c.getClassName() + "#" + c.getMethodName() + "(line " + c.getLineNumber()
                + ")");
        logger.warn("");
    }

    @Override
    public void edit(FieldAccess f) throws CannotCompileException {
        // logger.warn("fieldAccess {}", f);
        // logger.warn("");
    }

    @Override
    public void edit(Handler h) throws CannotCompileException {
        // logger.warn("handler {}", h);
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
    public void edit(NewExpr e) throws CannotCompileException {
        try {
            CtConstructor constructor = e.getConstructor();
            CtBehavior where = e.where();
            Instrumentation instrumentation = new Instrumentation(e.getClassName(), constructor.getName(),
                    e.getLineNumber());

            if (fileTransformer.alreadyInstrumented(instrumentation)) {
                return;
            }
            fileTransformer.addInstrumentation(instrumentation);

            logger.warn("NewExpr {}", instrumentation);
            logger.warn("");

            constructor.insertBeforeBody(instrumentation.getBeforeBody());
            constructor.insertAfter(instrumentation.getAfter());
            constructor.instrument(new ProfilingExprEditor(fileTransformer, constructor.getDeclaringClass()));

            Transformation transformation = fileTransformer
                    .getTransformation(constructor.getDeclaringClass().getName());
            if (transformation != null) {
                java.lang.instrument.Instrumentation javainstrumentation = fileTransformer.getInstrumentation();

                javainstrumentation.addTransformer(new ProfilingClassFileTransformer(javainstrumentation), true);
                // javainstrumentation.addTransformer(new ProfilingClassFileTransformer(javainstrumentation));
                // Class class1 = e.getConstructor().getDeclaringClass().toClass();
                // Class<?> class1 = constructor.getDeclaringClass().toClass(transformation.getLoader(),
                // transformation.getProtectionDomain());
                // Class[] allLoadedClasses = javainstrumentation.getAllLoadedClasses();
                // for (Class class2 : allLoadedClasses) {
                // try {
                // javainstrumentation.retransformClasses(class2);
                // } catch (Exception e22) {
                // e22.printStackTrace();
                // }
                // }

                Class<?> cls1 = Class.forName(constructor.getDeclaringClass().getName());
                // if (javainstrumentation.isModifiableClass(cls1)) {
                // javainstrumentation.retransformClasses(cls1);
                // }

                ClassDefinition classDefinition = new ClassDefinition(cls1, constructor.getDeclaringClass()
                        .toBytecode());
                // javainstrumentation.retransformClasses(cls1);
                // javainstrumentation.redefineClasses();
                javainstrumentation.redefineClasses(classDefinition);

            }
        } catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
        }

    }
}
