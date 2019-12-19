package com.healthsparq.app.conf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Bean
	@Qualifier("schemaList")
	public List<String> schemaList() {
		List<String> schemas =  new ArrayList<>();
		schemas.add("xproduct");
		schemas.add("cost");
		
		return schemas;
	}
}
