package de.twenty11.unitprofiler.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @author carsten
 * 
 */
public abstract class AbstractUnitProfilerMojo extends AbstractMojo {

    /**
     * Maven project.
     * 
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;

    protected abstract void executeMojo() throws MojoExecutionException, MojoFailureException;

    public final void execute() throws MojoExecutionException, MojoFailureException {
        if ("pom".equals(project.getPackaging())) {
            getLog().info("Skipping unitProfiler as packaging type is 'pom'");
            return;
        }
        executeMojo();
    }

    /**
     * @return Maven project
     */
    protected final MavenProject getProject() {
        return project;
    }

}
