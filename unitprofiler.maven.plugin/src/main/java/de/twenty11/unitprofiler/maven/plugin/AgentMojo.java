package de.twenty11.unitprofiler.maven.plugin;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.util.StringUtils;

/**
 * Prepares a property pointing to the unitProfiler runtime agent that can be passed as a VM argument to the application
 * under test. Depending on the project packaging type by default a property with the following name is set:
 * <ul>
 * <li>tycho.testArgLine for packaging type eclipse-test-plugin and</li>
 * <li>argLine otherwise.</li>
 * </ul>
 * Resulting coverage information is collected during execution and by default written to a file when the process
 * terminates.
 * 
 * @phase initialize
 * @goal prepare-agent
 * @requiresProject true
 * @requiresDependencyResolution runtime
 * @threadSafe
 */
public class AgentMojo extends AbstractUnitProfilerMojo {

    private static final String AGENT_ARTIFACT_NAME = "de.twentyeleven.unitprofiler:unitprofiler.core";
    private static final String SUREFIRE_ARG_LINE = "argLine";

    /**
     * @parameter expression="${plugin.artifactMap}"
     * @required
     * @readonly
     */
    private Map<String, Artifact> pluginArtifactMap;

    private String propertyName;

    @Override
    public void executeMojo() {
        String formattedString = "-javaagent:" + getAgentJarFile().getAbsolutePath();
        getLog().info(formattedString);
        final String vmArgument = StringUtils.quoteAndEscape(formattedString, '"');
        getLog().info(vmArgument);
        prependProperty(vmArgument);
    }

    private void prependProperty(final String vmArgument) {
        if (isPropertyNameSpecified()) {
            getLog().info("propertynamespecified");
            prependProperty(propertyName, vmArgument);
        } else {
            getLog().info("propertynamespecified  - Else");
            prependProperty(SUREFIRE_ARG_LINE, vmArgument);
        }
    }

    private File getAgentJarFile() {
        final Artifact unitProfilerAgentJar = pluginArtifactMap.get(AGENT_ARTIFACT_NAME);
        return unitProfilerAgentJar.getFile();
    }

    private boolean isPropertyNameSpecified() {
        return propertyName != null && !"".equals(propertyName);
    }

    private void prependProperty(final String name, final String value) {
        final Properties projectProperties = getProject().getProperties();
        final String oldValue = projectProperties.getProperty(name);
        final String newValue = oldValue == null ? value : value + ' ' + oldValue;
        getLog().info(name + " set to " + newValue);
        projectProperties.put(name, newValue);
    }

}
