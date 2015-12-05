package io.moquette.fce.model.common;

/**
 * Result enum for a valdation check.
 * 
 * @author lants1
 *
 */
public enum CheckResult {

	VALID(Boolean.TRUE),

	INVALID(Boolean.FALSE),

	NO_RESULT(null);

	private Boolean value;

	private CheckResult(Boolean value) {
		this.value = value;
	}

	public Boolean getValue() {
		return (value);
	}
}
