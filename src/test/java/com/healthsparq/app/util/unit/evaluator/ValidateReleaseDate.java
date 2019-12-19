package com.healthsparq.app.util.unit.evaluator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.healthsparq.app.util.Evaluator;

@SpringBootTest
public class ValidateReleaseDate {

	@Autowired
	private Evaluator evaluator;
	
	@Test
	public void given_date_has_wrong_format_when_is_validated_it_should_fail() {
		
		//given date has wrong format
		final String date = "2019-13-12";
		
		//when is validated
		final Boolean isValid = evaluator.isValidReleaseDate(date);
		
		//then it should fail
		Assertions.assertFalse(isValid);
	}
}
