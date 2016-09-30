# FaunaDB JVM Drivers

[![Build Status](https://img.shields.io/travis/faunadb/faunadb-jvm/master.svg?maxAge=21600)](https://travis-ci.org/faunadb/faunadb-jvm)
[![Maven Central](https://img.shields.io/maven-central/v/com.faunadb/faunadb-common.svg?maxAge=21600)]()
[![License](https://img.shields.io/badge/license-MPL_2.0-blue.svg?maxAge=2592000)](https://raw.githubusercontent.com/faunadb/faunadb-jvm/master/LICENSE)

This repository contains the FaunaDB drivers for the JVM languages. Currently, Java and Scala drivers are implemented.

### Features

* All drivers fully support the current version of the [FaunaDB API](https://fauna.com/documentation).
* All per-language drivers share the same underlying library [faunadb-common](./faunadb-common).
* Supports [Dropwizard Metrics](https://dropwizard.github.io/metrics/3.1.0/) hooks for stats reporting.

## Installation

Download from the Maven central repository:

### faunadb-java/pom.xml:

```xml
  <dependencies>
  ...
  <dependency>
    <groupId>com.faunadb</groupId>
    <artifactId>faunadb-java</artifactId>
    <version>0.3.2</version>
    <scope>compile</scope>
  </dependency>
  ...
</dependencies>
```

### faunadb-scala/sbt

```scala
libraryDependencies += ("com.faunadb" %% "faunadb-scala" % "0.3.2")
```

## Documentation

Javadocs and Scaladocs are hosted on GitHub:

* [faunadb-java](http://faunadb.github.io/faunadb-jvm/0.3.2/faunadb-java/api/)
* [faunadb-scala](http://faunadb.github.io/faunadb-jvm/0.3.2/faunadb-scala/api/)

## Dependencies

### Shared

* [Jackson](https://github.com/FasterXML/jackson) for JSON parsing.
* [Async HTTP client](https://github.com/AsyncHttpClient/async-http-client) and [Netty](http://netty.io/) for the HTTP transport.
* [Joda Time](http://www.joda.org/joda-time/) for date and time manipulation.

### Java

* Java 7
* [Google Guava](https://github.com/google/guava), for collections and ListenableFutures.

### Scala

* Scala 2.11.x

## Building

The faunadb-jvm project is built using sbt:

* **sbt**: [Scala Simple Build Tool](http://www.scala-sbt.org/)

To build and run tests against cloud, set the env variable
`FAUNA_ROOT_KEY` to your admin key secret and run `sbt test` from the
project directory.

To run tests against an enterprise cluster or developer instance, you
will also need to set `FAUNA_SCHEME` (http or https), `FAUNA_DOMAIN`
and `FAUNA_PORT`.

### License

All projects in this repository are licensed under the [Mozilla Public License](./LICENSE)