package de.twenty11.unitprofile.agent;

import java.lang.instrument.Instrumentation;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.twenty11.unitprofile.domain.MethodInvocation;
import de.twenty11.unitprofiler.annotations.Profile;

/**
 * A javaagent providing a premain method which adds the {@link ProfilingClassFileTransformer} to the provided
 * {@link Instrumentation} class.
 * 
 * The {@link ProfilingClassFileTransformer} will search for {@link Profile}-annotated test methods and will add
 * profiling instrumentation to all the code "below" that methods.
 * 
 * The whole process is done in a couple of steps:
 * 
 * 1) Instrumentation: see {@link ProfilingClassFileTransformer} 
 * 
 * 2) Profiling: see {@link ProfilingCallback} 
 * 
 * 3) Calculation 
 * 
 * 4) Output Rendering 
 * 
 */
public class Agent {

    private static final Logger logger = LoggerFactory.getLogger(Agent.class);

    private static ProfilingClassFileTransformer transformer;

    private static MethodInvocation rootInvocation;

    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("Starting instrumentation for profiling...");
        transformer = new ProfilingClassFileTransformer(inst);
        if (inst.isRetransformClassesSupported()) {
            inst.addTransformer(transformer, true);
        } else {
            logger.warn("Retransformation is not supported be the current JVM...");
            logger.warn("No profiling will be performed by unitprofiler.");
        }
    }

    public static List<de.twenty11.unitprofile.domain.MethodDescriptor> getInstrumentations() {
        return transformer.getInstrumentations();
    }

    public static void setRootInvocation(MethodInvocation rootInvocation) {
        Agent.rootInvocation = rootInvocation;
    }

    public static MethodInvocation getRootInvocation() {
        return rootInvocation;
    }

}
