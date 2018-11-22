/**
 Copyright [2018] [Ole Lensmar]

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 **/

package io.nanoservices.samples.aws;

import io.nanoservices.serverless.annotations.Function;

import java.util.Map;

/**
 * Simple handler to illustrate usage of Function annotation for automatic packaging of lambda functions for AWS
 */

public class HelloWorldHandler {
    @Function
    public String sayHello( Map<String, Object> input ){
        return "Hello " + input.get( "name");
    }

    @Function( "sayHelloWorld")
    public String sayHelloWorld(){
        return "Hello world!";
    }
}
