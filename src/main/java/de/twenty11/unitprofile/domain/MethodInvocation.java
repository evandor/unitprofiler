package de.twenty11.unitprofile.domain;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class MethodInvocation {

    private int lineNumber;
    private List<Clock> durations = new ArrayList<Clock>();
    
    private static ArrayDeque<Clock> clocks = new ArrayDeque<Clock>();
    
    private String cls;
    private String method;
    private List<MethodInvocation> children = new ArrayList<MethodInvocation>();
    private double timeShare;
    DecimalFormat df = new DecimalFormat("#.00");
    DecimalFormat longFormat = new DecimalFormat("###,##0"); 
    private MethodInvocation parent;
    private double selfTimeShare;

    public MethodInvocation(MethodDescriptor methodDescriptor) {
        this(null, methodDescriptor);
    }

    public MethodInvocation(MethodInvocation parent, MethodDescriptor methodDescriptor) {
        newTimer();
        this.cls = methodDescriptor.getClassName();
        this.method = methodDescriptor.getMethodName();
        this.parent = parent;
        this.lineNumber = methodDescriptor.getLineNumber();
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public void increment() {
        newTimer();
    }

    private void addChild(MethodInvocation invocation) {
        this.children.add(invocation);

    }

    public void setEnd(long currentTimeMillis) {
        Clock newestTimer = clocks.pollLast();
        newestTimer.stop();
    }
    
    public MethodInvocation getParent() {
        return parent;
    }

    public String getCls() {
        return cls;
    }

    public String getMethod() {
        return method;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public List<MethodInvocation> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(cls).append("#").append(method).append(" ")
                .append((getTime()));
        sb.append(", ").append(children.size()).append(" children");
        return sb.toString();
    }

    public long getTime() {
        long time = 0;
        for(Clock timer : durations) {
            time += timer.getElapsed();
        }
        return time;
    }
    
    public long getSelfTime() {
        long childrenTime = 0;
        for(MethodInvocation child : children) {
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
//        for (int i = 0; i < depth; i++) {
//            sb.append("  ");
//        }
        sb.append(cls).append("#").append(method).append(" (").append("").append("): ")
                .append((getTime()));
        sb.append(" ").append(timeShare).append("%");
        sb.append("\n");
        if (children.size() != 0) {
            for (MethodInvocation invocation : children) {
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
            parentTime = getTime();
        } else {
            setTimeShare(100.0 * getTime() / parentTime);
        }
        setSelfTimeShare(100.0 * getSelfTime() / getTime());
        
        for (MethodInvocation invocation : children) {
            invocation.calc(parentTime);
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
        sb.append("   <th align='right'>Time (ms)</th>\n");
        sb.append("   <th align='right'>Time (%)</th>\n");
        sb.append("   <th>&nbsp;</th>\n");
        sb.append("   <th align='right'>Self Time (ms)</th>\n");
        sb.append("   <th align='right'>Self Time (%)</th>\n");
        sb.append("   <th align='right'>Count</th>\n");
        sb.append("   <th align='right'>Time/Invocation</th>\n");
        sb.append(" </tr>\n");
        sb.append("</thead>\n");
        sb.append("<tbody>\n");

        sb.append(rowInTreeTable(this, 0, null));

        sb.append("</tbody>\n");
        sb.append("</table>");
        return sb.toString();
    }

    public String rowInTreeTable(MethodInvocation inv, int indentation, Integer parentId) {
        StringBuilder sb = new StringBuilder();
        Integer id = indentation;

        sb.append("\n<tr data-tt-id='").append(id).append("'");
        if (parentId != null) {
            sb.append(" data-tt-parent-id='").append(parentId).append("'");
        }
        sb.append(">");
        sb.append("<td align='left'>").append(inv.cls).append("#").append(inv.method).append(" (").append(inv.getLineNumber()).append(")").append("</td>");
        
        // time (ms)
        sb.append("<td align='right'>").append((longFormat.format(inv.getTime()))).append("</td>");

        // time (%)
        sb.append("<td align='right'>").append(df.format(inv.timeShare)).append("% </td>");

        // bar
        sb.append("<td align='left'>");
        sb.append("<span class='graph'><span style='width: ").append(Math.round(inv.timeShare)).append("px;' class='bar'></span></span>");
        sb.append("</td>");
        
        sb.append("</td>");
        sb.append("<td align='right'>").append(inv.getSelfTime()).append("</td>");
        sb.append("<td align='right'>").append(df.format(inv.selfTimeShare)).append("%</td>");
        sb.append("<td align='right'>").append(inv.getCount()).append("</td>");
        sb.append("<td align='right'>").append(inv.getTime() / inv.getCount()).append("</td>");
        sb.append("</tr>");
        if (inv.children.size() != 0) {
            for (MethodInvocation child : inv.children) {
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
