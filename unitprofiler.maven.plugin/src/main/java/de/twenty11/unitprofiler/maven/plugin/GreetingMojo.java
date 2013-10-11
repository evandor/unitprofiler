package de.twenty11.unitprofiler.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

public class GreetingMojo extends AbstractMojo {
    public void execute() throws MojoExecutionException {
        getLog().info("Hello, world.");
    }
}
