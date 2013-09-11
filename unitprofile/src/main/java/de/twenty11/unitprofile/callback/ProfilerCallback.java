package de.twenty11.unitprofile.callback;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import de.twenty11.unitprofile.Profiler;
import de.twenty11.unitprofile.agent.Invocation;

public class ProfilerCallback {
    
    static int indention = 0;
    
    private static boolean profiling = false;

    private static long offset;
    
    private static List<Invocation> invocations = new ArrayList<Invocation>();
    
    private static ArrayDeque<Invocation> callstack = new ArrayDeque<Invocation>();

    //private static Invocation startInvocation;

    public static Profiler start (String objectName, String methodName) {
        // TODO what if start is called multiple times?
        profiling = true;
        offset = System.currentTimeMillis();
        System.out.println(indentBy(indention) + objectName + "#" + methodName + " (0) 0");
        Invocation startInvocation = new Invocation(objectName, methodName);
        invocations.add(startInvocation);
        callstack.add(startInvocation);
        indention++;
        return new Profiler(startInvocation);
    }

    public static void stop  (String objectName, String methodName) {
        profiling = false;
        indention--;
        long now = System.currentTimeMillis();
        System.out.println(indentBy(indention) + objectName + "#" + methodName + " (0) " + (now - offset));
        invocations.get(invocations.size()-1).setEnd(now);
        callstack.pollLast();
    }

    public static void before (String objectName, String methodName, int depth) {
        if (!profiling) {
            return;
        }
        long now = System.currentTimeMillis();
        Invocation topOfStackInvocation = callstack.peekLast();
        new Invocation(topOfStackInvocation, objectName, methodName);
        //invocations.add(invocation);
        System.out.println(indentBy(indention) + objectName + "#" + methodName + " ("+depth+") " + (now - offset));
        indention++;
    }
    
    public static void after (String objectName, String methodName, int depth) {
        if (!profiling) {
            return;
        }
        long elapsed = System.currentTimeMillis() - offset;
        if (indention > 0) {
            indention--;
        }
        System.out.println(indentBy(indention) + objectName + "#" + methodName + " ("+depth+") " + elapsed);
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
