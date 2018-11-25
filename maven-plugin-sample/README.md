* Maven Plugin Sample

This project shows how to use the annotations and plugin with aws and openwhisk - simply build with 

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

If you change the provider element in the plugin configuration to "openwhisk" and optionally add your APIHOST 
and AUTH values to the config as shown in the example below:

```
<plugin>
    <groupId>io.nanoservices</groupId>
    <artifactId>serverless-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <provider>openwhisk</provider>
        <providerConfig>
           <ApiHost>...</ApiHost>
           <Auth>...</Auth>
        </providerConfig>
    </configuration>
    <executions>
      ...
    </executions>
</plugin>
```
 
you can simply rerun the above commands and now deploy to openwhisk instead; the generated serverless.yml file
will now contain configuration for the [HelloWorldApp](src/main/java/io/nanoservices/samples/openwhisk/HelloWorldApp.java) 
class instead of the AWS classes extracted by the aws provider.


