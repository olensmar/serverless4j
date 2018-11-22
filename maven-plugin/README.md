# Serverless Maven Plugin

A maven plugin for making it easier to use the [serverless](https://serverless.com) framework with java. 
The plugin currently:
* automatically generates the serverless.yml file based on introspection of project code
* provides wrapper mvn goals for serverless commands

<b>Please note - this plugin is under development, please open issues or pull-requests 
if have requests/suggestions/bugs/etc... - thank you!</b> 

## Installation

* Install the serverless framework as described at [Getting started with serverless](https://serverless.com/framework/docs/getting-started/)
* Add the plugin to your pom under the build/plugins section:
```
<plugin>
    <groupId>io.nanoservices</groupId>
    <artifactId>serverless-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <provider>aws</provider>
    </configuration>
    <executions>
        <execution>
            <phase>process-classes</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Until there is a non-SNAPSHOT version of the plugin you'll also need to add the sonatype snapshot repository
to your list of repositories:

```
<pluginRepositories>
    <pluginRepository>
        <id>sonatype-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </pluginRepository>
</pluginRepositories>
```

## Usage

Once configured as above you can simply create serverless functions/handlers in accordance with your target providers 
 platform and build your project; the plugin will generate a serverless.yml file for your selected provider into a 
 target/serverless folder. Provided you've configured serverless for your provider you can use the 
 
```
mvn serverless:deploy
```

command to deploy the generated artifact to your target platform (this simply runs the corresponding serverless deploy 
for the generated serverless.yml).

If you want to invoke one of the deployed functions you can use

```
mvn serverless:invoke -Dfunction=<functionName>
```
 
Depending on which provider you've specified in the plugin configuration, handlers are added to serverless.yml 
as follows:

* "aws" - the plugin will extract
  * any classes implementing the AWS RequestHandler / RequestStreamHandler interfaces
  * any methods annotated with the [Function](../annotations/src/main/java/io/nanoservices/serverless/annotations/Function.java) annotation
* "openwhisk" - the plugin will find all classes conforming to the [OpenWhisk requirement](https://console.bluemix.net/docs/openwhisk/openwhisk_actions.html#creating-java-actions)
and add them as handlers to the generated serverless.yml file. Use the 
[Function]((../annotations/src/main/java/io/nanoservices/serverless/annotations/Function.java)) annotation to name the handler 
(otherwise it will be named with the class name).   
* (more to come)

 
