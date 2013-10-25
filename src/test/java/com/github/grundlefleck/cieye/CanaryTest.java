package com.github.grundlefleck.cieye;

import org.junit.Test;

public class CanaryTest {

	
	@Test public void checkTestsFail() {
		Canary.throwSomething();
	}
}
