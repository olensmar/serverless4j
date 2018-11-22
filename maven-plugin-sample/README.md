* Maven Plugin Sample

This project shows how to use the annotations and plugin - simply build with 

```
mvn clean install
```

and then deploy to aws (provided you've configured the serverless framework correctly) with

```
mvn serverless:deploy
```

If you look in the target/serverless folder you'll see the serverless.yml file generated from the handlers
in the [HelloWorldHandler](src/main/java/io/nanoservices/samples/aws/HelloWorldHandler.java) and
[HelloWorldRequestHandler](src/main/java/io/nanoservices/samples/aws/HelloWorldRequestHandler.java) classes.
