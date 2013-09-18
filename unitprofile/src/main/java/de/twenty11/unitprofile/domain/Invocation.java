package de.twenty11.unitprofile.domain;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Invocation {

    private List<Clock> durations = new ArrayList<Clock>();
    
    private static ArrayDeque<Clock> clocks = new ArrayDeque<Clock>();
    
    private String cls;
    private String method;
    private List<Invocation> children = new ArrayList<Invocation>();
    private int depth;
    private double timeShare;
    DecimalFormat df = new DecimalFormat("#.00");
    private Invocation parent;

    private double selfTimeShare;

    public Invocation(String objectName, String methodName) {
        this(null, objectName, methodName, 0);
    }

    public Invocation(Invocation parent, String cls, String method, int depth) {
        newTimer();
        this.cls = cls;
        this.method = method;
        this.depth = depth;
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }
    
    public void increment() {
        newTimer();
    }

    private void addChild(Invocation invocation) {
        this.children.add(invocation);

    }

    public void setEnd(long currentTimeMillis) {
        Clock newestTimer = clocks.pollLast();
        newestTimer.stop();
    }
    
    public Invocation getParent() {
        return parent;
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
        sb.append(cls).append("#").append(method).append(" (").append(depth).append("): ")
                .append((getTime()));
        sb.append(", ").append(children.size()).append(" children");
        return sb.toString();
    }

    private long getTime() {
        long time = 0;
        for(Clock timer : durations) {
            time += timer.getElapsed();
        }
        return time;
    }
    
    private long getSelfTime() {
        long childrenTime = 0;
        for(Invocation child : children) {
            childrenTime += child.getTime();
        }
        return getTime() - childrenTime;
    }
    
    private int getCount() {
        return durations.size();
    }

    public String dump() {
        return dump(0);
    }

    private String dump(int indentation) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        sb.append(cls).append("#").append(method).append(" (").append(depth).append("): ")
                .append((getTime()));
        sb.append(" ").append(timeShare).append("%");
        sb.append("\n");
        if (children.size() != 0) {
            for (Invocation invocation : children) {
                sb.append(invocation.dump(++indentation));
            }
        }
        return sb.toString();
    }

    public void calc() {
        calc(null);
    }

    private void calc(Long parentTime) {
        if (parentTime == null) {
            setTimeShare(100.0);
        } else {
            setTimeShare(100.0 * getTime() / parentTime);
        }
        setSelfTimeShare(100.0 * getSelfTime() / getTime());
        
        for (Invocation invocation : children) {
            invocation.calc(getTime());
        }
    }

    private void setTimeShare(double d) {
        this.timeShare = d;
    }
    
    private void setSelfTimeShare(double d) {
        this.selfTimeShare = d;
    }

    public String treetable() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table id=\"treetable1\">");

        sb.append("<caption>\n");
        sb.append("<a href='#' onclick=\"jQuery('#treetable1').treetable('expandAll'); return false;\">Expand all</a>\n");
        sb.append("<a href='#' onclick=\"jQuery('#treetable1').treetable('collapseAll'); return false;\">Collapse all</a>\n");
        sb.append("</caption>\n");
        sb.append("<thead>\n");
        sb.append(" <tr>\n");
        sb.append("   <th>Method</th>\n");
        sb.append("   <th>Time (ms)</th>\n");
        sb.append("   <th>Time (%)</th>\n");
        sb.append("   <th>Self Time (ms)</th>\n");
        sb.append("   <th>Self Time (%)</th>\n");
        sb.append("   <th>Count</th>\n");
        sb.append("   <th>Time/Invocation</th>\n");
        sb.append(" </tr>\n");
        sb.append("</thead>\n");
        sb.append("<tbody>\n");

        sb.append(rowInTreeTable(this, 0, null));

        sb.append("</tbody>\n");
        sb.append("</table>");
        return sb.toString();
    }

    public String rowInTreeTable(Invocation inv, int indentation, Integer parentId) {
        StringBuilder sb = new StringBuilder();
        Integer id = indentation;

        sb.append("\n<tr data-tt-id='").append(id).append("'");
        if (parentId != null) {
            sb.append(" data-tt-parent-id='").append(parentId).append("'");
        }
        sb.append(">");
        sb.append("  <td>").append(inv.cls).append("#").append(inv.method).append("</td>");
        sb.append("<td>").append((inv.getTime())).append("</td>");
        sb.append("<td>").append(df.format(inv.timeShare)).append("% ");
        
        sb.append("<div class='graph'><div style='width: ").append(Math.round(inv.timeShare)).append("px;' class='bar'></div></div>");
        
        sb.append("</td>");
        sb.append("<td>").append(inv.getSelfTime()).append("</td>");
        sb.append("<td>").append(df.format(inv.selfTimeShare)).append("%</td>");
        sb.append("<td>").append(inv.getCount()).append("</td>");
        sb.append("<td>").append(inv.getTime() / inv.getCount()).append("</td>");
        sb.append("</tr>");
        if (inv.children.size() != 0) {
            for (Invocation child : inv.children) {
                sb.append(rowInTreeTable(child, ++indentation, id));
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private void newTimer() {
        Clock timer = new Clock();
        durations.add(timer);
        clocks.add(timer);
    }

}
