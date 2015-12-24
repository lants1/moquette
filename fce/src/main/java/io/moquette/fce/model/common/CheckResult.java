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

	/**
	 * Gets the value of a CheckResult enum.
	 * 
	 * @return value Boolean
	 */
	public Boolean getValue() {
		return value;
	}
}
