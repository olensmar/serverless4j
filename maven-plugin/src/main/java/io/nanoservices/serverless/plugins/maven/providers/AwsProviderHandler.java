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

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import io.nanoservices.serverless.annotations.Function;
import io.nanoservices.serverless.plugins.maven.ProviderHandler;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.project.MavenProject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ProviderHandler for AWS
 */
public class AwsProviderHandler implements ProviderHandler {
    private final Map params;

    public AwsProviderHandler(Map params) {
        this.params = params;
    }

    /**
     * Finds methods that comply with the RequestHandler / RequestStreamHandler interfaces or have the
     * Function annotation
     *
     * @param aClass the class possibly containing function handlers
     * @return a list of found methods
     */

    @Override
    public Collection<Method> findFunctions(Class aClass) {

        Set<Class> interfaces = Sets.newHashSet(aClass.getInterfaces());
        if (interfaces.contains(RequestHandler.class) || interfaces.contains(RequestStreamHandler.class)) {
            Set<Method> methods = Sets.newHashSet();

            for (Method m : aClass.getMethods()) {
                if (m.getName().equals("handleRequest")) {
                    methods.add(m);
                }
            }

            return methods;
        }

        return
            Collections2.filter(
                Arrays.asList(MethodUtils.getMethodsWithAnnotation(aClass, Function.class)),
                method -> ((method.getModifiers() & Modifier.STATIC) == 0) && ((method.getModifiers() & Modifier.PUBLIC) != 0));
    }

    @Override
    public void createHandlerConfig(Method method, Map<String, Object> handlers, MavenProject project) {
        Function annotation = method.getAnnotation(Function.class);
        String name = annotation != null && !annotation.value().isEmpty() ? annotation.value() : method.getName();

        Set<Class> interfaces = Sets.newHashSet(method.getDeclaringClass().getInterfaces());

        if (interfaces.contains(RequestHandler.class) || interfaces.contains(RequestStreamHandler.class)) {
            handlers.put(name, new HashMap<String, String>() {{
                put("handler", method.getDeclaringClass().getName());
            }});
        } else if (annotation != null) {
            handlers.put(name, new HashMap<String, String>() {{
                put("handler", method.getDeclaringClass().getName() + "::" + method.getName());
            }});
        }
    }

    @Override
    public void enhanceConfig(Map<String, Object> config, MavenProject project) {
        Map<String, String> providerConfig = (Map<String, String>) config.get("provider");
        providerConfig.put("runtime", "java8");
    }

    @Override
    public void beforeServerlessCli(ProcessBuilder builder) {

    }
}
