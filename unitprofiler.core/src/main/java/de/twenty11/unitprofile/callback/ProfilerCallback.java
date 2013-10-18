package de.twenty11.unitprofile.callback;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.twenty11.unitprofile.agent.Agent;
import de.twenty11.unitprofile.domain.MethodDescriptor;
import de.twenty11.unitprofile.domain.MethodInvocation;
import de.twenty11.unitprofile.output.OutputGenerator;

/**
 * the class called from the instrumented methods.
 * 
 */
public class ProfilerCallback {

    private static final Logger logger = LoggerFactory.getLogger(ProfilerCallback.class);

    /**
     * contains all profiling information after profiling
     */
    private static List<MethodInvocation> invocations = new ArrayList<MethodInvocation>();

    /**
     * a stack keeping track of the nested method calls. Starts empty and stops with the top-level (root) invocation.
     */
    private static ArrayDeque<MethodInvocation> callstack = new ArrayDeque<MethodInvocation>();

    /**
     * the first invocation for this profiling session.
     * 
     * @param objectName
     * @param methodName
     * @return
     */
    public static MethodInvocation start(String objectName, String methodName, int lineNumber) {
        bigMessage("Starting profiling... " + objectName + "#" + methodName + " (" + lineNumber + ")");
        if (profiling()) {
            logger.error("Profiling was already started for '{}'", callstack.getFirst().getCls() + "#"
                    + callstack.getFirst().getMethod());
            throw new IllegalStateException();
        }

        MethodDescriptor methodDescriptor = new MethodDescriptor(objectName, methodName, lineNumber);
        MethodInvocation rootInvocation = new MethodInvocation(methodDescriptor);
        invocations.add(rootInvocation);
        callstack.add(rootInvocation);
        Agent.setRootInvocation(rootInvocation);
        return rootInvocation;
    }

    public static void stop(String objectName, String methodName) {
        bigMessage("Profiling... done.");

        long now = System.currentTimeMillis();
        invocations.get(invocations.size() - 1).setEnd(now);
        MethodInvocation last = callstack.pollLast();

        logger.info("Calculating data...");

        last.calc();

        logger.info("Profiling output:\n");
        logger.info(last.dump());

        logger.info("Creating files...");

        new OutputGenerator().renderFromBootstrapTemplate(last);
        new OutputGenerator().renderDebugInfo();

        logger.info("Instrumentation execution... done.");
    }

    public static void before(String objectName, String methodName, int lineNumber) { // , int depth) {
        if (!profiling()) {
            return;
        }
        handleInvocation(objectName, methodName, lineNumber);
    }

    public static void after(String objectName, String methodName) {
        if (!profiling()) {
            return;
        }
        long now = System.currentTimeMillis();
        MethodInvocation pollLast = callstack.pollLast();
        pollLast.setEnd(now);
    }

    public static boolean profiling() {
        return callstack.size() > 0;
    }

    public static List<MethodInvocation> getInvocations() {
        return invocations;
    }

    private static void handleInvocation(String objectName, String methodName, int lineNumber) {
        MethodInvocation existingInvocation = getInvocation(callstack.peekLast(), objectName, methodName);
        if (existingInvocation != null) {
            // logger.debug("invocation '{}' exists, incrementing count", existingInvocation);
            existingInvocation.increment();
            callstack.add(existingInvocation);
            return;
        }
        MethodDescriptor methodDescriptor = new MethodDescriptor(objectName, methodName, lineNumber);
        MethodInvocation invocation = new MethodInvocation(callstack.peekLast(), methodDescriptor);
        logger.info("invocation of '{}'", invocation);
        invocations.add(invocation);
        callstack.add(invocation);
    }

    private static MethodInvocation getInvocation(MethodInvocation peekLast, String objectName, String methodName) {
        for (MethodInvocation invocation : invocations) {
            if (invocation.getParent() == null && peekLast != null) {
                continue;
            }
            if (invocation.getParent().equals(peekLast) && invocation.getCls().equals(objectName)
                    && invocation.getMethod().equals(methodName)) {
                return invocation;
            }
        }
        return null;
    }

    private static void bigMessage(String msg) {
        logger.info("");
        logger.info("=====================");
        logger.info(msg);
        logger.info("=====================");
        logger.info("");
    }

}
