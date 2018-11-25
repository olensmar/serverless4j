package io.nanoservices.serverless.plugins.maven.mojos;

import io.nanoservices.serverless.plugins.maven.ProviderHandler;
import io.nanoservices.serverless.plugins.maven.providers.AwsProviderHandler;
import io.nanoservices.serverless.plugins.maven.providers.OpenWhiskProvider;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Map;

/**
 * Created on 2018-11-25.
 */
public abstract class BaseMojo extends AbstractMojo {
    /**
     * Serverless provider to use - aws and openwhisk supported for now
     */

    @Parameter(property = "provider", defaultValue = "aws")
    protected String provider;

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;
    /**
     * Configuration passed to provider
     */

    @Parameter(property = "providerConfig")
    private Map providerConfig;

    public static String getArtifactPath(MavenProject project) {
        return project.getBuild().getDirectory() + File.separatorChar + project.getBuild().getFinalName() + "." + project.getArtifact().getType();
    }

    protected ProviderHandler createProviderHandler() {
        if ("aws".equals(provider)) {
            return new AwsProviderHandler(providerConfig);
        } else if ("openwhisk".equals(provider)) {
            return new OpenWhiskProvider(providerConfig);
        }

        throw new RuntimeException("Unsupported provider: " + provider);
    }
}
