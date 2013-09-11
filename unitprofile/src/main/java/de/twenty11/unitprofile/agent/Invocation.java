package de.twenty11.unitprofile.agent;

import java.util.ArrayList;
import java.util.List;

public class Invocation {

    private long start;
    private long end;
    private String cls;
    private String method;
    private List<Invocation> children = new ArrayList<Invocation>();

    public Invocation(String objectName, String methodName) {
        this (null, objectName, methodName);
    }

    public Invocation(Invocation parent, String cls, String method) {
        this.cls = cls;
        this.method = method;
        start = System.currentTimeMillis();
        if (parent != null) {
            parent.addChild(this);
        }
    }
   
    private void addChild(Invocation invocation) {
        this.children.add(invocation);
        
    }

    public void setEnd(long currentTimeMillis) {
        end = currentTimeMillis;
    }
    
    public long getStart() {
        return start;
    }
    
    public long getEnd() {
        return end;
    }
    
    public String getCls() {
        return cls;
    }
    
    public String getMethod() {
        return method;
    }
    
    public List<Invocation> getChildren() {
        return children;
    }

}
