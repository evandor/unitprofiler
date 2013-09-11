package de.twenty11.unitprofile.agent;

import java.util.ArrayList;
import java.util.List;

public class Invocation {

    private long start;
    private long end;
    private String cls;
    private String method;
    private List<Invocation> children = new ArrayList<Invocation>();
    private int depth;
    private double timeShare;

    public Invocation(String objectName, String methodName) {
        this(null, objectName, methodName, 0);
    }

    public Invocation(Invocation parent, String cls, String method, int depth) {
        this.cls = cls;
        this.method = method;
        this.depth = depth;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(cls).append("#").append(method).append(" (").append(depth).append("): ").append((end - start));
        sb.append(", ").append(children.size()).append(" children");
        return sb.toString();
    }

    public String dump() {
        return dump(0);
    }

    private String dump(int indentation) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        sb.append(cls).append("#").append(method).append(" (").append(depth).append("): ").append((end - start));
        sb.append(" ").append(timeShare).append("%");
        sb.append("\n");
        if (children.size() != 0) {
            for (Invocation invocation : children) {
                sb.append(invocation.dump(++indentation));
            }
        } else {
            // sb.append("\n");
        }
        return sb.toString();
    }

    public void calc() {
        calc(null);
    }

    private void calc(Long parentTime) {
        Long myTime = end - start;
        if (parentTime == null) {
            setTimeShare(100.0);
        } else {
            setTimeShare(100.0 * myTime  / parentTime);
        }
        
        for (Invocation invocation : children) {
            invocation.calc(myTime);
        }
    }

    private void setTimeShare(double d) {
        this.timeShare = d;
    }

}
