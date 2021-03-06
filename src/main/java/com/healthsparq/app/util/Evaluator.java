package com.healthsparq.app.util;

import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Evaluator {
	
	@Autowired
	@Qualifier("schemaList")
	private List<String> schemas;
	
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	
	public Boolean isExistingSchema(String name) {
		return schemas.contains(name.toLowerCase());
	}
	
	public Boolean isValidReleaseDate(String text) {
		try {
				var dateFormat = new SimpleDateFormat(DATE_FORMAT);
				var date = dateFormat.parse(text);
				return date != null;
			
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
}
