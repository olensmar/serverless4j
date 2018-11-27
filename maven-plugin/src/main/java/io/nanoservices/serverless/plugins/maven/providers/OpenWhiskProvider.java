/**
 * Copyright [2018] [Ole Lensmar]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package io.nanoservices.serverless.plugins.maven.providers;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import io.nanoservices.serverless.annotations.Function;
import io.nanoservices.serverless.plugins.maven.ProviderHandler;
import io.nanoservices.serverless.plugins.maven.mojos.GenerateMojo;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.project.MavenProject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProviderHandler for openwhisk
 */
public class OpenWhiskProvider implements ProviderHandler {
    private final Map openwhiskParams;

    public OpenWhiskProvider(Map openwhiskParams) {
        this.openwhiskParams = openwhiskParams;
    }

    @Override
    public List<Method> findFunctions(Class aClass) {
        ArrayList<Method> result = Lists.newArrayList();

        Method mainMethod = MethodUtils.getMatchingAccessibleMethod(aClass, "main", JsonObject.class);
        if (mainMethod != null && isOpenWhiskActionMethod(mainMethod)) {
            result.add(mainMethod);
        }

        return result;
    }

    private boolean isOpenWhiskActionMethod(Method mainMethod) {
        return mainMethod.getReturnType().equals(JsonObject.class) &&
            ((mainMethod.getModifiers() & Modifier.STATIC) != 0) &&
            ((mainMethod.getModifiers() & Modifier.PUBLIC) != 0);
    }

    @Override
    public void createHandlerConfig(Method method, Map<String, Object> handlers, MavenProject project) {
        Function annotation = method.getAnnotation(Function.class);
        String name = annotation != null && !annotation.value().isEmpty() ? annotation.value() : method.getDeclaringClass().getSimpleName();

        handlers.put(name, new HashMap<String, String>() {{
            put("handler", GenerateMojo.getArtifactPath(project) + ":" + method.getDeclaringClass().getName());
        }});
    }

    @Override
    public void enhanceConfig(Map<String, Object> config, MavenProject project) {
        Map<String, String> providerConfig = (Map<String, String>) config.get("provider");
        providerConfig.put("runtime", "java");

        if (!config.containsKey("plugins")) {
            config.put("plugins", Lists.newArrayList());
        }

        List plugins = (List) config.get("plugins");
        plugins.add("serverless-openwhisk");
    }

    @Override
    public void beforeServerlessCli(ProcessBuilder builder) {
        if (openwhiskParams.containsKey("ApiHost")) {
            builder.environment().put("OW_APIHOST", String.valueOf(openwhiskParams.get("ApiHost")));
        }
        if (openwhiskParams.containsKey("Auth")) {
            builder.environment().put("OW_AUTH", String.valueOf(openwhiskParams.get("Auth")));
        }
    }
}
