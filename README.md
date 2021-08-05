# JDA Appender
Library for Java applications that use [JDA](https://github.com/DV8FromTheWorld/JDA) to easily stream
the logging output of the application to a text channel on Discord.

[insert screenshot here]

# Usage
Using default values:

```java
ChannelLoggingHandler handler = new ChannelLoggingHandler(() -> jda.getTextChannelById(System.getenv("CHANNEL")))
        .attachJavaLogging() // attach to java.util.logging
        .schedule(); // schedule handler to flush output asynchronously every second
```

Customizing config values, such as adding a logger name mapping:

```java
ChannelLoggingHandler handler = new ChannelLoggingHandler(() -> jda.getTextChannelById(System.getenv("CHANNEL")), config -> {
    config.setColored(true); // enable coloring of different log levels, default true
    config.setSplitCodeBlockForLinks(false); // split the output code blocks when a link is present, default false
    config.setAllowLinkEmbeds(true); // when splitting code blocks for links, allow the links to have an embed, default true
    config.mapLoggerName("net.dv8tion.jda", "JDA"); // add a mapping for logger names "net.dv8tion.jda*" to just be "JDA"
}).attachJavaLogging().schedule();
```

# Artifact
```xml
<repository>
    <id>scarsz</id>
    <url>https://nexus.scarsz.me/content/repositories/releases/</url>
</repository>
```
```xml
<dependency>
    <groupId>me.scarsz</groupId>
    <artifactId>jdaappender</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

# Using with SLF4J-backed apps with minimal effort (JDA, ...)

Add `slf4j-jdk14` as a dependency. This will forward SLF4J messages to java.util.logging and can be
picked up with `ChannelLoggingHandler#attachJavaLogging`.

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>1.7.31</version>
</dependency>
```
