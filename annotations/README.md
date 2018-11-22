# Serverless Annotations

Annotations used by the Maven Plugin to identify functions/handlers and provide required metadata in code.

For now there is only one; [Function](src/main/java/io/nanoservices/serverless/annotations/Function.java) - that is
used to mark class methods as function handlers.

Add to your build dependencies with

```
<dependency>
    <groupId>io.nanoservices</groupId>
    <artifactId>serverless-annotations</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
        
and annotate methods that you want to expose as function handlers:

```java
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
```

Read more about usage for the [Maven Plugin](../maven-plugin/README.md)

