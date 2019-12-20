package com.healthsparq.app.util.unit.evaluator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.healthsparq.app.util.Evaluator;

@SpringBootTest
public class ValidateReleaseDateTest {

	@Autowired
	private Evaluator evaluator;
	
	@Test
	public void given_date_has_wrong_order_when_is_validated_it_should_fail() {
		
		//given date has wrong order
		final String date = "2019-13-12";
		
		//when is validated
		final Boolean isValid = evaluator.isValidReleaseDate(date);
		
		//then it should fail
		Assertions.assertFalse(isValid);
	}
	
	@Test
	public void given_date_has_wrong_delimiters_when_is_validated_it_should_fail() {
		
		//given date has wrong format
		final String date = "2019/12/12";
		
		//when is validated
		final Boolean isValid = evaluator.isValidReleaseDate(date);
		
		//then it should fail
		Assertions.assertFalse(isValid);
	}
	
	@Test
	public void given_date_has_wrong_values_when_validated_it_should_fail() {
		
		//given date has wrong format
		final String date = "2020-13-23";
		
		//when is validated
		final Boolean isValid = evaluator.isValidReleaseDate(date);
		
		//then it should fail
		Assertions.assertFalse(isValid);
	}
	

	@Test
	public void given_date_follows_all_the_rules_when_validated_it_should_pass() {
		
		//given date has wrong format
		final String date = "2020-12-1";
		
		//when is validated
		final Boolean isValid = evaluator.isValidReleaseDate(date);
		
		//then it should fail
		Assertions.assertFalse(isValid);
	}
}
