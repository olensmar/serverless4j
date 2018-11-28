package io.nanoservices.serverless.plugins.maven.providers.aws;

import com.google.common.collect.Maps;
import io.nanoservices.serverless.plugins.maven.providers.AwsProviderHandler;
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

public class AwsProviderHandlerTest {

    AwsProviderHandler providerHandler = new AwsProviderHandler(Maps.newHashMap());

    @Test
    public void testFindMethods() {
        assertTrue(providerHandler.findFunctions(AwsProviderHandlerTest.class).isEmpty());

        Collection<Method> methods = providerHandler.findFunctions(AwsHandler.class);
        assertEquals(1, methods.size());

        methods = providerHandler.findFunctions(AwsRequestHandler.class);
        assertEquals(1, methods.size());

        methods = providerHandler.findFunctions(AwsRequestStreamHandler.class);
        assertEquals(1, methods.size());

        methods = providerHandler.findFunctions(AwsRequestAndRequestStreamHandler.class);
        assertEquals(2, methods.size());
    }

    @Test
    public void testEnhanceConfig() {
        Map<String, Object> config = Maps.newHashMap();
        config.put("provider", Maps.<String, Object>newHashMap());
        providerHandler.enhanceConfig(config, mock(MavenProject.class));
        assertEquals("java8", ((Map<String, Object>) config.get("provider")).get("runtime"));
    }

    @Test
    public void testCreateHandlerConfig() {
        Map<String, Object> config = Maps.newHashMap();

        providerHandler.createHandlerConfig(providerHandler.findFunctions(AwsHandler.class).get(0), config, mock(MavenProject.class));
        assertNotNull(config.get("sayHelloWorld"));
        assertEquals("io.nanoservices.serverless.plugins.maven.providers.aws.AwsHandler::sayHelloWorld",
            ((Map<String, Object>) config.get("sayHelloWorld")).get("handler"));

        config.clear();
        providerHandler.createHandlerConfig(providerHandler.findFunctions(AwsRequestHandler.class).get(0), config, mock(MavenProject.class));
        assertNotNull(config.get("helloWorldHandler"));
        assertEquals("io.nanoservices.serverless.plugins.maven.providers.aws.AwsRequestHandler",
            ((Map<String, Object>) config.get("helloWorldHandler")).get("handler"));

        config.clear();
        providerHandler.createHandlerConfig(providerHandler.findFunctions(AwsRequestStreamHandler.class).get(0), config, mock(MavenProject.class));
        assertNotNull(config.get("helloWorldStreamHandler"));
        assertEquals("io.nanoservices.serverless.plugins.maven.providers.aws.AwsRequestStreamHandler",
            ((Map<String, Object>) config.get("helloWorldStreamHandler")).get("handler"));

        config.clear();
        List<Method> functions = providerHandler.findFunctions(AwsRequestAndRequestStreamHandler.class);
        providerHandler.createHandlerConfig(functions.get(0), config, mock(MavenProject.class));
        providerHandler.createHandlerConfig(functions.get(1), config, mock(MavenProject.class));

        assertNotNull(config.get("helloWorldHandler"));
        assertEquals("io.nanoservices.serverless.plugins.maven.providers.aws.AwsRequestAndRequestStreamHandler",
            ((Map<String, Object>) config.get("helloWorldHandler")).get("handler"));

        assertNotNull(config.get("helloWorldStreamHandler"));
        assertEquals("io.nanoservices.serverless.plugins.maven.providers.aws.AwsRequestAndRequestStreamHandler",
            ((Map<String, Object>) config.get("helloWorldStreamHandler")).get("handler"));
    }
}
