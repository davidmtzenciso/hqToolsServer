package com.healthsparq.app.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Evaluator {
	
	@Autowired
	@Qualifier("schemaList")
	private List<String> schemas;

	public Boolean isExistingSchema(String name) {
		return schemas.contains(name.toLowerCase());
	}
	
	public Boolean isValidReleaseDate(String date) {
		return null;
	}
}
