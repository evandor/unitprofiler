package de.twenty11.unitprofile.callback;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import de.twenty11.unitprofile.agent.Invocation;
import de.twenty11.unitprofile.output.OutputGenerator;

public class ProfilerCallback {

    static int indention = 0;

    private static boolean profiling = false;

    private static long offset;

    private static List<Invocation> invocations = new ArrayList<Invocation>();

    private static ArrayDeque<Invocation> callstack = new ArrayDeque<Invocation>();

    public static Invocation start(String objectName, String methodName) {
        // TODO what if start is called multiple times (without stopping first)?
        profiling = true;
        offset = System.currentTimeMillis();
        System.out.println(indentBy(indention) + objectName + "#" + methodName + " (0) 0");
        Invocation rootInvocation = new Invocation(objectName, methodName);
        invocations.add(rootInvocation);
        callstack.add(rootInvocation);
        indention++;
        return rootInvocation;
    }

    public static void stop(String objectName, String methodName) {
        profiling = false;
        indention--;
        long now = System.currentTimeMillis();
        System.out.println(indentBy(indention) + objectName + "#" + methodName + " (0) " + (now - offset));
        invocations.get(invocations.size() - 1).setEnd(now);
        Invocation last = callstack.pollLast();
        
        last.calc();
        
        System.out.println("\n=====================\n");
        System.out.println(last.dump());
        
        new OutputGenerator().renderFromBootstrapTemplate(last);
    }

    public static void before(String objectName, String methodName) { //, int depth) {
        if (!profiling) {
            return;
        }
        long now = System.currentTimeMillis();
        Invocation topOfStackInvocation = callstack.peekLast();
        System.out.println(indentBy(indention) + objectName + "#" + methodName + " (" + callstack.size() + ") " + (now - offset));
        callstack.add(new Invocation(topOfStackInvocation, objectName, methodName, callstack.size()));
        indention++;
    }

    public static void after(String objectName, String methodName) {
        if (!profiling) {
            return;
        }
        long now = System.currentTimeMillis();
        indention--;
        Invocation pollLast = callstack.pollLast();
        pollLast.setEnd(now);
        System.out.println(indentBy(indention) + objectName + "#" + methodName + " (" + callstack.size() + ") " + (now -offset));
    }

    private static String indentBy(int indention) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2 * indention; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static boolean isProfiling() {
        return profiling;
    }

    public static List<Invocation> getInvocations() {
        return invocations;
    }
}
