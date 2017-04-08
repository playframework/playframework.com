# Java Deserialization vulnerability in Logback SocketAppender

## Date

April 7th 2017

## Description

A deserialization vulnerability has been [found](https://logback.qos.ch/news.html) in the [socket appender and socket receiver](https://logback.qos.ch/manual/appenders.html#SocketAppender) in Logback, which is used by Play. This affects all versions of Play from 2.0.0 through 2.5.13.

Play includes integration with Logback through SLF4J.  Logback has functionality that enables logging events to be sent over a network, using Java Serialization.

Using Logback in Play itself does not result in vulnerability as per the [default Play configuration](https://www.playframework.com/documentation/2.5.x/SettingsLogger), but if Logback has been specifically configuration to use SocketAppender or ServerSocketReceiver, then Play is vulnerable.

## Impact

This vulnerability is filed as [CVE-2017-5929](https://nvd.nist.gov/vuln/detail/CVE-2017-5929).  As with most Java deserialization bugs, deserializing untrusted input leads to remote command execution in the JVM.

Because Play is not configured out of the box using SocketAppender, and because SocketAppender does not fit into production logging environments (i.e. syslog or ELK stack), it is unlikely that production environments are impacted by this vulnerability.  There is a possibility of development environments being configured explicitly using SocketAppender to integrate into IDEs or desktop logging tools.

## Affected Versions

Play 2.0.0 - 2.5.13

## Workarounds

The workaround for this vulnerability is to upgrade the Logback library to version 1.2.3, which does not have these issues.

// https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

## Fixes

Upgrade to Play 2.5.14, upgrade the Logback library if you are on previous versions of Play, or remove references to SocketAppender or SocketReceiver from Logback configuration.

Ideally, [disable Java serialization altogether](https://tersesystems.com/2015/11/08/closing-the-open-door-of-java-object-serialization/) using [notsoserial](https://github.com/kantega/notsoserial) or other Java agent based system.

## Acknowledgements

Thanks to Joel Berta for reporting this issue.