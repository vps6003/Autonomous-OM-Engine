package com.vps.omengine;

import com.vps.omengine.config.GroqProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GroqProperties.class)
public class OmengineApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmengineApplication.class, args);
	}

}
