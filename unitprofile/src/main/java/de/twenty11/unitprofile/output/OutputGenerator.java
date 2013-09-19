package de.twenty11.unitprofile.output;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import de.twenty11.unitprofile.agent.Agent;
import de.twenty11.unitprofile.domain.Instrumentation;
import de.twenty11.unitprofile.domain.Invocation;

public class OutputGenerator {

    private static final Logger logger = LoggerFactory.getLogger(OutputGenerator.class);

    private static final String OUTPUT_PATH = "target/site/unitprofile";

    public void renderFromBootstrapTemplate(Invocation rootInvocation) {
        try {
            InputStream resourceAsStream = this.getClass().getResourceAsStream("/templates/treetable.stg");
            StringWriter writer = new StringWriter();
            IOUtils.copy(resourceAsStream, writer);
            ST indexFile = new ST(writer.toString(), '$', '$');

            // ST indexFile = new ST(FileUtils.readFileToString(treetableFile), '$', '$');
            indexFile.add("dump", rootInvocation.dump());
            indexFile.add("treetable", rootInvocation.treetable());
            FileUtils.writeStringToFile(new File(OUTPUT_PATH + "/" + getOutputFileName(rootInvocation)), indexFile.render());
        
            MyFileUtils.copyResourcesRecursively(this.getClass().getResource("/bootstrap"), new File(OUTPUT_PATH));
            MyFileUtils.copyResourcesRecursively(this.getClass().getResource("/css"), new File(OUTPUT_PATH));
            MyFileUtils.copyResourcesRecursively(this.getClass().getResource("/ludo-jquery-treetable"), new File(OUTPUT_PATH));
        
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void renderDebugInfo() {
        List<Instrumentation> instrumentations = Agent.getInstrumentations();
        Collections.sort(instrumentations);
        StringBuilder sb = new StringBuilder();
        for (Instrumentation instrumentation : instrumentations) {
            sb.append(instrumentation.toString()).append("\n");
        }
        try {
            FileUtils.writeStringToFile(new File(OUTPUT_PATH + "/inst." + getOutputFileName(Agent.getRootInvocation())), sb.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getOutputFileName(Invocation rootInvocation) {
        return rootInvocation.getCls() + "." + rootInvocation.getMethod() + ".html";
    }
}
