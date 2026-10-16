package me.cobrex.townymenu.utils;

import org.junit.jupiter.api.Test;

public final class ComponentPromptCancelTest {
	@Test
	void recognizesLocalizedAndFallbackCancellation() {
		main(new String[0]);
	}

	public static void main(String[] args) {
		assertResult(true, ComponentPrompt.isCancelInput("cancelar", "cancelar"), "localized cancellation");
		assertResult(true, ComponentPrompt.isCancelInput("CANCEL", "cancelar"), "English fallback cancellation");
		assertResult(false, ComponentPrompt.isCancelInput("123", "cancelar"), "ordinary input");
		assertResult(false, ComponentPrompt.isCancelInput(null, "cancelar"), "null input");

		System.out.println("4 localized prompt cancellation tests passed.");
	}

	private static void assertResult(boolean expected, boolean actual, String label) {
		if (expected != actual)
			throw new AssertionError(label + ": expected " + expected + ", got " + actual);
		System.out.println("PASS " + label);
	}
}
