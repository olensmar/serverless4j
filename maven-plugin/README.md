# Serverless Maven Plugin

A maven plugin for making it (a little) easier to use the [serverless](https://serverless.com) framework 
with java, it currently:
* automatically generates the serverless.yml file based on introspection of project code
* provides wrapper mvn goals for serverless commands

#### Contents
* [Installation](#installation)
* [Usage](#usage)
* [serverless.yml generation](#serverlessyml-generation)
  * [AWS](#aws)
  * [OpenWhisk](#openwhisk)
* [Goals and configuration properties](#goals-and-configuration-properties)
  * [serverless:generate](#serverlessgenerate)
  * [serverless:deploy](#serverlessdeploy)
  * [serverless:invoke](#serverlessinvoke)
* [Future Plans](#future-plans)

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
(the plugin currently supports aws and openwhisk providers)

Until there is a published non-SNAPSHOT version of the plugin available in maven central you'll 
need to either clone this repo and build it with `mvn clean install`, or add the sonatype snapshot repository
to your list of repositories, either in mavens settings.xml or in your project pom.xml;

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

Once configured as above you can create serverless functions/handlers in accordance with your target providers 
 platform and build your project; the plugin will generate a serverless.yml file for your selected provider into the 
 target/serverless folder during your build. Provided you've configured serverless for your provider you can then use 
 
```
mvn serverless:deploy
```

to deploy the generated artifact to your target platform (this simply runs the corresponding serverless deploy 
for the generated serverless.yml).

If you want to invoke one of the deployed functions you can use

```
mvn serverless:invoke -Dfunction=<functionName>
```
 
## serverless.yml generation

For now the plugin will generate the provider, service, package and functions properties; depending on which 
provider you've specified in the plugin configuration, function handlers are extracted from your classes and added 
to serverless.yml as described below.

### AWS

When targeting AWS use the string "aws" for the provider; the plugin will look for 

* all classes implementing the AWS `RequestHandler` / `RequestStreamHandler` interfaces
* all methods annotated with the [Function](../annotations/src/main/java/io/nanoservices/serverless/annotations/Function.java) annotation

and add these to the generated serverless.yml file accordingly.

See the [AWS Sample](../maven-plugin-sample/src/main/java/io/nanoservices/samples/aws) package and containing 
[Plugin Sample project](../maven-plugin-sample) for a concrete example.

### OpenWhisk

When targeting OpenWhisk use the string "openwhisk" for the provider; the plugin will extract 

* all classes conforming to the [OpenWhisk requirement](https://console.bluemix.net/docs/openwhisk/openwhisk_actions.html#creating-java-actions)

and add them as handlers to the generated serverless.yml file. As with AWS you can use the 
[Function]((../annotations/src/main/java/io/nanoservices/serverless/annotations/Function.java)) annotation to name the handler 
(otherwise it will be named with the class name).   

See the [OpenWhisk Sample](../maven-plugin-sample/src/main/java/io/nanoservices/samples/openwhisk) pacakge and containing 
[Plugin Sample project](../maven-plugin-sample) for a concrete example.

If you want to specify the OW_APIHOST and OW_AUTH values directly in the pom you can do so using the providerParams
configuration element:

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
        <execution>
            <phase>process-classes</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
 
## Goals and configuration properties

All below goals require the specification of which provider to use, currently "aws" and "openwhisk" are supported.

### `serverless:generate`

This goal would usually be associated with the process-classes build phase in maven (as in the example above)

* `service` (optional) - the service name specified in serverless.yml - defaults to the artifactId.
* `source` (optional) - path to a template serverless.yml file used as base for the generated file.

### `serverless:deploy`

This goal could be associated with the corresponding maven build phase - but can just as well be run manually. It
uses the serverless.yml file generated by serverless:generate to run the corresponding serverless deploy command.

### `serverless:invoke` 

This is a standalone goal for invoking a deployed function - as shown in the example above.

* `function` (required) - name of the function to invoke. 

## Future plans...   

If anyone actually finds this useful or promising then future functionality could be to 
* support more providers
* provide the possibility to verify deployed functions
* provide an abstraction layer that enables the exact same java code to work on all providers
* <whatever you come up with!>

Please get in touch or open issues accordingly!

