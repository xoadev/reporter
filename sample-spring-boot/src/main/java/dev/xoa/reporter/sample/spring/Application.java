package dev.xoa.reporter.sample.spring;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	@Bean
	public ApiReporter apiReporter(MeterRegistry meterRegistry) {
		return new ApiReporterImpl(meterRegistry);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}