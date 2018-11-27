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

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.nanoservices.serverless.annotations.Function;

import java.util.Map;

/**
 * Simple AWS RequestHandler to illustrate automatic packaging and deployment
 */

public class AwsRequestHandler implements RequestHandler<Map<String, Object>, String> {

    @Override
    @Function("helloWorldHandler")
    public String handleRequest(Map<String, Object> input, Context context) {
        return "Hello " + input.get("name");
    }
}
