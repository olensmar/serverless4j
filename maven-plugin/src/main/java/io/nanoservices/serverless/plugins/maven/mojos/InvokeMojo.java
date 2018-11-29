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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Maven goal for invoking a deployed serverless function using "serverless invoke"
 */

@Mojo(name = "invoke",
    defaultPhase = LifecyclePhase.DEPLOY,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    threadSafe = true)
public class InvokeMojo extends BaseMojo {

    @Parameter(property = "function", required = true)
    private String function;

    /**
     * Executes the serverless invoke command in the generated serverless directory, requires the target
     * function name to be specified with -Dfunction=...
     */

    public void execute() {

        try {
            ProcessBuilder builder = new ProcessBuilder("serverless", "invoke", "--function", function)
                .inheritIO()
                .directory(new File(project.getBuild().getDirectory(), "serverless"))
                .redirectErrorStream(true);

            createProviderHandler().beforeServerlessCli(builder);

            Process process = builder.start();
            process.waitFor(60, TimeUnit.SECONDS);
            getLog().debug("Process exited with exitValue " + process.exitValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}