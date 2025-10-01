package com.example.oktausersvc;

import com.example.oktausersvc.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = AppProperties.class)
// Alternatively: @ConfigurationPropertiesScan("com.example.oktausersvc")
public class JavaOauthServiceAppExampleApplication {
	public static void main(String[] args) {
		SpringApplication.run(JavaOauthServiceAppExampleApplication.class, args);
	}
}
