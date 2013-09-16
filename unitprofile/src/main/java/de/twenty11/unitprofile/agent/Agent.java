package de.twenty11.unitprofile.agent;

import java.lang.instrument.Instrumentation;
import java.util.List;

public class Agent {
    
    private static ProfilingClassFileTransformer transformer;
    
    public static void premain(String agentArgs, Instrumentation inst) {
        
        System.out.println("Starting instrumentation for profiling...");
        System.out.println("=========================================");

        transformer = new ProfilingClassFileTransformer();
        inst.addTransformer(transformer);
    }

    public static List<de.twenty11.unitprofile.domain.Instrumentation> getInstrumentations() {
        return transformer.getInstrumentations();
    }
    
}
