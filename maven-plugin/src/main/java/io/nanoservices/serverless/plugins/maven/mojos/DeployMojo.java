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
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;

@Mojo(name = "deploy",
    defaultPhase = LifecyclePhase.DEPLOY,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    threadSafe = true)
public class DeployMojo extends BaseMojo {

    /**
     * Executes the serverless deploy command in the generated serverless directory
     */

    public void execute() {

        try {
            ProcessBuilder builder = new ProcessBuilder("serverless", "deploy")
                .inheritIO()
                .directory(new File(project.getBuild().getDirectory(), "serverless"))
                .redirectErrorStream(true);

            createProviderHandler().beforeServerlessCli(builder);

            Process process = builder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}