package de.twenty11.unitprofile.agent;

import java.lang.instrument.Instrumentation;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Agent {
    
    private static final Logger logger = LoggerFactory.getLogger(Agent.class);

    private static ProfilingClassFileTransformer transformer;
    
    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("Starting instrumentation for profiling...");
        transformer = new ProfilingClassFileTransformer();
        inst.addTransformer(transformer);
    }

    public static List<de.twenty11.unitprofile.domain.Instrumentation> getInstrumentations() {
        return transformer.getInstrumentations();
    }

}
