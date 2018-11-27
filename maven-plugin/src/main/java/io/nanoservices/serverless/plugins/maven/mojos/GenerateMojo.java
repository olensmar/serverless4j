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

package io.nanoservices.serverless.plugins.maven.mojos;

import com.google.common.collect.Lists;
import io.nanoservices.serverless.plugins.maven.ProviderHandler;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mojo(name = "generate",
    defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    threadSafe = true)
public class GenerateMojo extends BaseMojo {

    /**
     * Name of service in generated serverless.ymla
     */

    @Parameter(property = "service", defaultValue = "${project.artifactId}")
    private String service;

    /**
     * Source serverless.yml to merge into generated one
     */

    @Parameter(property = "source")
    private File source;

    /**
     * Extracts functions from built classes and generates corresponding serverless.yml file
     */

    public void execute() {

        try {
            ProviderHandler providerHandler = createProviderHandler();

            List<Method> handlers = findFunctionHandlers(providerHandler);
            getLog().debug("Found " + handlers.size() + " Function handlers");
            if (!handlers.isEmpty()) {
                createServerlessYml(handlers, providerHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Method> findFunctionHandlers(ProviderHandler providerHandler) throws IOException {
        Path root = new File(project.getBuild().getOutputDirectory()).toPath();
        HandlerFinder handlerFinder = new HandlerFinder(providerHandler);
        Files.walkFileTree(root, handlerFinder);

        return handlerFinder.handlerMethods;
    }

    private void createServerlessYml(List<Method> handlerMethods, ProviderHandler providerHandler) throws IOException {
        Map<String, Object> data = new HashMap<>();

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        if (source != null && source.exists()) {
            data.putAll(yaml.load(new FileInputStream(source)));
        }

        data.put("service", service);

        data.put("provider", new HashMap<String, String>() {{
            put("name", provider);
        }});

        data.put("package", new HashMap<String, String>() {{
            put("artifact", getArtifactPath(project));
        }});

        Map<String, Object> handlers = data.containsKey("functions") ? (Map<String, Object>) data.get("functions") : new HashMap<>();
        handlerMethods.forEach(e -> {
            providerHandler.createHandlerConfig(e, handlers, project);
        });

        data.put("functions", handlers);
        providerHandler.enhanceConfig(data, project);

        File serverless = new File(project.getBuild().getDirectory(), "serverless");
        if (!serverless.exists()) {
            serverless.mkdir();
        }

        yaml.dump(data, new FileWriter(new File(serverless, "serverless.yml")));
        getLog().info("Created serverless.yml with " + handlers.size() + " function" + ((handlers.size() == 1 ? "" : "s")));
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    private class HandlerFinder extends SimpleFileVisitor<Path> {

        private final ProviderHandler providerHandler;
        private ClassPool classPool = ClassPool.getDefault();
        private List<Method> handlerMethods = Lists.newArrayList();

        public HandlerFinder(ProviderHandler providerHandler) {

            this.providerHandler = providerHandler;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

            try {
                if (file.getFileName().toString().endsWith(".class")) {
                    CtClass clazz = classPool.makeClass(new FileInputStream(file.toFile()));
                    handlerMethods.addAll(providerHandler.findFunctions(clazz.toClass()));
                }
            } catch (Exception e) {
                throw new IOException(e);
            }

            return FileVisitResult.CONTINUE;
        }
    }
}

