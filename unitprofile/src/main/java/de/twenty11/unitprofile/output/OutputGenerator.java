package de.twenty11.unitprofile.output;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.stringtemplate.v4.ST;

import de.twenty11.unitprofile.agent.Invocation;

public class OutputGenerator {

    private static final String OUTPUT_PATH = "target/site/unitprofile";

    public void renderFromBootstrapTemplate(Invocation rootInvocation) {
        try {
            ST indexFile = new ST(FileUtils.readFileToString(new File("src/main/resources/templates/index.stg")), '$','$');
            indexFile.add("dump", rootInvocation.dump());
            indexFile.add("treetable", rootInvocation.treetable());
            FileUtils.writeStringToFile(new File(OUTPUT_PATH + "/index.html"), indexFile.render());
            FileUtils.copyDirectory(new File("src/main/resources/bootstrap"), new File(OUTPUT_PATH));
            FileUtils.copyDirectory(new File("src/main/resources/ludo-jquery-treetable"), new File(OUTPUT_PATH));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
