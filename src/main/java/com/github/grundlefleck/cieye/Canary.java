package com.github.grundlefleck.cieye;

import java.io.IOException;

import org.netmelody.cieye.core.observation.CiSpy;

public class Canary {

	public static void throwSomething() {
		throw new UnsupportedOperationException();
	}
	
	public void referenceSomethingInARestrictedPartOfCieye() throws IOException {
		CiSpy anythingFromSpiesIsOkay = null;
	}

}
