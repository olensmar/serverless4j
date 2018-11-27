package io.nanoservices.serverless.plugins.maven.providers;

import io.nanoservices.serverless.annotations.Function;

public class AwsHandler {
    @Function("sayHelloWorld")
    public String sayHelloWorld() {
        return "Hello world!";
    }

    @Function("sayHelloWorld")
    private String privateSayHelloWorld() {
        return "Hello world!";
    }

    @Function("sayHelloWorld")
    public static String staticSayHelloWorld() {
        return "Hello world!";
    }
}
