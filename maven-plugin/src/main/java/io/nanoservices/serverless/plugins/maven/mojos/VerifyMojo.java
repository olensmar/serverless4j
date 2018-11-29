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
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Maven goal for verifying the existence and return value of deployed functions using the "serverless invoke" command
 */

@Mojo(name = "verify",
    defaultPhase = LifecyclePhase.INTEGRATION_TEST,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    threadSafe = true)
public class VerifyMojo extends BaseMojo {

    @Parameter(property = "verify")
    protected Function[] verify;

    @Override
    public void execute() throws MojoFailureException {
        for (Function function : verify) {
            verifyFunction(function);
        }
    }

    private void verifyFunction(Function function) throws MojoFailureException {
        try {
            getLog().info("Verifying function [" + function.getName() + "]");

            List<String> commands = Lists.newArrayList("serverless", "invoke", "--function", function.getName());
            if (function.getInput() != null) {
                commands.add("--data");
                commands.add(function.getInput());
            }

            File outFile = File.createTempFile("verify-output", ".log");
            outFile.deleteOnExit();

            ProcessBuilder builder = new ProcessBuilder(commands)
                .redirectOutput(outFile)
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectErrorStream(true)
                .directory(new File(project.getBuild().getDirectory(), "serverless"));

            createProviderHandler().beforeServerlessCli(builder);

            Process process = builder.start();
            process.waitFor(function.getTimeout(), TimeUnit.SECONDS);
            if (process.isAlive()) {
                process.destroy();
            }

            List<String> lines = Files.readAllLines(outFile.toPath());
            if (function.isDebug()) {
                lines.forEach(line -> getLog().info(line));
            }

            if (process.exitValue() != 0) {
                getLog().info("Invocation of [" + function.getName() + "] failed - exitCode " + process.exitValue());
                lines.forEach(line -> getLog().error(line));
                throw new MojoFailureException("Invocation of [" + function.getName() + "] failed");
            }

            String line = lines.isEmpty() ? null : lines.get(0);

            if (function.getExpect() != null) {
                if (function.getExpect().equals(line)) {
                    getLog().info("Function [" + function.getName() + "] returned expected [" + line + "]");
                } else {
                    throw new MojoFailureException(this, "Function verification failed",
                        "Function [" + function.getName() + "] returned [" + line + "] instead of expected [" +
                            function.getExpect() + "]");
                }
            }
        } catch (MojoFailureException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoFailureException(this, "Function verification failed",
                "Failed to verify function [" + function.getName() + "]");
        }
    }
}
