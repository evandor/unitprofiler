package de.twenty11.unitprofile.domain;

import java.util.ArrayList;
import java.util.List;

import javassist.NotFoundException;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;


/**
 * Describing object of an instrumented method.
 * 
 * The identity is comprised of the provided classname and the method name (the line number
 * is only for information purposes).
 *
 */
public class MethodDescriptor implements Comparable<MethodDescriptor>{
    
    private final int lineNumber;
    private String className;
    private String methodName;

    private List<MethodInvocation> invocations = new ArrayList<MethodInvocation>();

    public MethodDescriptor(String className, String methodName, int lineNumber) {
        this.className = className;
        this.methodName = methodName;
        this.lineNumber = lineNumber;
    }
    
    public MethodDescriptor(NewExpr newExpr) throws NotFoundException {
        this(newExpr.getClassName(), newExpr.getConstructor().getName(), newExpr.getLineNumber());   
    }

    public MethodDescriptor(MethodCall mc) {
        this(mc.getClassName(), mc.getMethodName(), mc.getLineNumber());
    }

    public void addInvocation(MethodInvocation invocation) {
        this.invocations.add(invocation);
    }

    public String getBeforeBody() {
        return "{ProfilerCallback.before(\""+className+"\", \""+methodName+"\", "+lineNumber+");}";
    }

    public String getAfter() {
        return "{ProfilerCallback.after(\""+className+"\", \""+methodName+"\");}";
    }
    
    public String getInsertBefore() {
        StringBuilder sb = new StringBuilder("{ProfilerCallback.before(\"");
        sb.append(className).append("\", \"");
        sb.append(methodName).append("\", ");
        sb.append(lineNumber).append(");}");
        return sb.toString();
    }
    
    public String getInsertAfter() {
        StringBuilder sb = new StringBuilder("{ProfilerCallback.after(\"");
        sb.append(className).append("\", \"");
        sb.append(methodName).append("\");}");
        return sb.toString();
    }

    
    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        //sb.append(thread).append(", ").append(object).append("#").append(method);
        sb.append(className).append("#").append(methodName);
        return sb.toString();
    }
    
    @Override
    public int compareTo(MethodDescriptor o) {
        return this.toString().compareTo(o.toString());
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        //result = prime * result + ((thread == null) ? 0 : thread.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MethodDescriptor other = (MethodDescriptor) obj;
        if (methodName == null) {
            if (other.methodName != null)
                return false;
        } else if (!methodName.equals(other.methodName))
            return false;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
       /* if (thread == null) {
            if (other.thread != null)
                return false;
        } else if (!thread.equals(other.thread))
            return false;    */
        return true;
    }

}
