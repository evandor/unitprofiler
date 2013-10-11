package de.twenty11.unitprofiler.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "sayhi")
public class GreetingMojo extends AbstractUnitProfilerMojo {

    @Override
    protected void executeMojo() throws MojoExecutionException, MojoFailureException {
        getLog().info("Hello, world.");
    }
}
