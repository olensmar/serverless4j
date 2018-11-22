package io.nanoservices.serverless.plugins.maven.providers;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import io.nanoservices.serverless.annotations.Function;
import io.nanoservices.serverless.plugins.maven.ProviderHandler;
import io.nanoservices.serverless.plugins.maven.mojos.GenerateMojo;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.project.MavenProject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2018-11-22.
 */
public class OpenWhiskProvider implements ProviderHandler {
    @Override
    public Collection<Method> findFunctions(Class aClass) {
        ArrayList<Method> result = Lists.newArrayList();

        Method mainMethod = MethodUtils.getMatchingAccessibleMethod( aClass, "main", JsonObject.class );
        if(mainMethod != null && isOpenWhiskActionMethod(mainMethod)){
            result.add(mainMethod);
        }

        return result;
    }

    private boolean isOpenWhiskActionMethod(Method mainMethod) {
        return mainMethod.getReturnType().equals(JsonObject.class) && ((mainMethod.getModifiers() & Modifier.STATIC) != 0);
    }

    @Override
    public void createHandlerConfig(Method method, Map<String, Object> handlers, MavenProject project) {
        Function annotation = method.getAnnotation(Function.class);
        String name = annotation != null && !annotation.value().isEmpty() ? annotation.value() : method.getDeclaringClass().getSimpleName();

        handlers.put(name, new HashMap<String, String>() {{
            put("handler", GenerateMojo.getArtifactPath( project ) + ":" + method.getDeclaringClass().getName());
        }});
    }

    @Override
    public void enhanceConfig(Map<String, Object> config, MavenProject project) {
        Map<String, String> providerConfig = (Map<String, String>) config.get("provider");
        providerConfig.put("runtime", "java");

        if( !config.containsKey("plugins")){
            config.put( "plugins", Lists.newArrayList());
        }

        List plugins = (List) config.get("plugins");
        plugins.add( "serverless-openwhisk");
    }
}

/**
 # you can add packaging information here
 package:
 artifact: target/demo-function.jar

 functions:
 demo:
 handler: com.example.FunctionApp

 # extend the framework using plugins listed here:
 # https://github.com/serverless/plugins
 plugins:
 - "serverless-openwhisk"
 */
