package io.nanoservices.serverless.plugins.maven.providers.openwhisk;

import com.google.common.collect.Maps;
import io.nanoservices.serverless.plugins.maven.providers.OpenWhiskProvider;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenWhiskProviderTest {
    OpenWhiskProvider providerHandler = new OpenWhiskProvider(Maps.newHashMap());

    @Test
    public void testFindMethods() {
        assertTrue(providerHandler.findFunctions(OpenWhiskProviderTest.class).isEmpty());

        Collection<Method> methods = providerHandler.findFunctions(OpenWhiskHelloWorldHandler.class);
        assertEquals(1, methods.size());

        methods = providerHandler.findFunctions(IncorrectOpenWhiskHelloWorldHandler1.class);
        assertTrue(methods.isEmpty());

        methods = providerHandler.findFunctions(IncorrectOpenWhiskHelloWorldHandler2.class);
        assertTrue(methods.isEmpty());
    }

    @Test
    public void testEnhanceConfig() {
        Map<String, Object> config = Maps.newHashMap();
        config.put("provider", Maps.<String, Object>newHashMap());
        providerHandler.enhanceConfig(config, mock(MavenProject.class));
        assertEquals("java", ((Map<String, Object>) config.get("provider")).get("runtime"));

        List pluginsList = (List) config.get("plugins");
        assertNotNull(pluginsList);
        assertTrue(pluginsList.contains("serverless-openwhisk"));
    }

    @Test
    public void testCreateHandlerConfig() {
        Map<String, Object> config = Maps.newHashMap();

        MavenProject mockProject = mock(MavenProject.class);
        Build mockBuild = mock(Build.class);
        Artifact mockArtifact = mock(Artifact.class);

        when(mockProject.getBuild()).thenReturn(mockBuild);
        when(mockProject.getArtifact()).thenReturn(mockArtifact);

        when(mockBuild.getDirectory()).thenReturn("/test");
        when(mockBuild.getFinalName()).thenReturn("test");
        when(mockArtifact.getType()).thenReturn("jar");

        providerHandler.createHandlerConfig(providerHandler.findFunctions(OpenWhiskHelloWorldHandler.class).get(0), config, mockProject);
        assertNotNull(config.get("HelloWorld"));
        assertEquals("/test/test.jar:io.nanoservices.serverless.plugins.maven.providers.openwhisk.OpenWhiskHelloWorldHandler",
            ((Map<String, Object>) config.get("HelloWorld")).get("handler"));
    }
}
