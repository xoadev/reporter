# Reporter

If you use a [clean architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) in your
applications, you may wonder where it is okay to use a log or metric library. Its use should be restricted to the outer
layers. A good way to use it in a use case would be to do it through an interface.

*Reporter* is designed to avoid coding the implementation of that interface and that your only job is to declare methods
with meaningful names.

## How to use

Just include the dependencies in maven or gradle.

### Configuration

#### Gradle java project

```kotlin
dependencies {
    compileOnly("dev.xoa.reporter:reporter-api:1.0.3")
    annotationProcessor("dev.xoa.reporter:reporter-generator:1.0.3")
}
```

#### Gradle kotlin project

```kotlin
plugins {
    kotlin("kapt")
}

dependencies {
    compileOnly("dev.xoa.reporter:reporter-api:1.0.3")
    kapt("dev.xoa.reporter:reporter-generator:1.0.3")
}
```

#### Maven java project

```xml
<dependences>
    <dependency>
      <groupId>dev.xoa.reporter</groupId>
      <artifactId>reporter-generator</artifactId>
      <version>1.0.3</version>
      <scope>provided</scope>
    </dependency>
</dependences>
```

### Reporter interface

Then annotate your interface as `@Reporter`. You can use underline characters to mark where put the
arguments.

```java
import dev.xoa.reporter.Reporter;

@Reporter
public interface YourReporter {

    void infoHello_Requested(String name);

    void incrementHelloRequestedWithName_(String name);
}
```

At compiling time the following implementation will be generated:

```java
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;

import io.micrometer.core.instrument.Tag;

@Component
public class YourReporterImpl implements YourReporter {

	private static final Logger log = LoggerFactory.getLogger(ApiReporterImpl.class);

	private final MeterRegistry meterRegistry;

	public ApiReporterImpl(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}
	
	@Override
	public void infoHello_Requested(java.lang.String name) {
		log.info("hello {} requested", name);
	}

	@Override
	public void incrementHelloRequestedWithName_(java.lang.String name) {
		String code = "hello.requested";
		Iterable<Tag> tags = java.util.Arrays.asList(Tag.of("name", name));
		this.meterRegistry.counter(code, tags).increment(1.0);
	}
}
```

### Use

So, you can use the interface:

```java
public class YourUseCase {

    private final YourReporter reporter;
    
    public YourUseCase(YourReporter reporter) {
        this.reporter = reporter;
    }

    public String invoke(String name) {
        reporter.infoHello_Requested(name);
        reporter.incrementHelloRequestedWithName_(name);

        return "Hello " + name + "!!!";
    }
}
```

## TODO

- [ ] What would be the correct package for the implementation?
- [ ] Complete tests
- [ ] Better documentation