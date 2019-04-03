package com.wl4g.devops.common.validation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public final class PhoneValidationValidator implements ConstraintValidator<PhoneValidation, String> {
	final private static ThreadLocal<Pattern> patternLocal = new InheritableThreadLocal<>();

	@Override
	public void initialize(PhoneValidation constraintAnnotation) {

	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.length() == 0) {
			return true;
		}
		return this.getPattern().matcher(value).matches();
	}

	private Pattern getPattern() {
		Pattern pattern = patternLocal.get();
		if (pattern == null) {
			patternLocal.set((pattern = Pattern.compile("^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$")));
		}
		return pattern;
	}

}
