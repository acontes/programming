package org.objectweb.proactive.core.security;

public enum Authorization {
	DENIED(-1), OPTIONAL(0), REQUIRED(1);

	private final int value;

	private Authorization(int value) {
		this.value = value;
	}

	public static Authorization compute(Authorization local,
			Authorization distant) throws IncompatiblePolicyException {
		return local.compute(distant);
	}

	public Authorization compute(Authorization that)
			throws IncompatiblePolicyException {
		if (this.value * that.value == -1) {
			throw new IncompatiblePolicyException("incompatible policies");
		}
		return realValue(this.value + that.value);
	}

	private Authorization realValue(int value) {
		if (value > 0) {
			return REQUIRED;
		}
		if (value < 0) {
			return DENIED;
		}
		return OPTIONAL;
	}

	public static Authorization fromString(String string) {
		for (Authorization value : Authorization.values()) {
			if (value.toString().equalsIgnoreCase(string)) {
				return value;
			}
		}
		return Authorization.DENIED;
	}
}