package com.github.grundlefleck.cieye;


import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.collect.Iterables.find;
import static com.google.common.io.Files.readLines;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.netmelody.cieye.core.domain.Status.BROKEN;
import static org.netmelody.cieye.core.domain.Status.GREEN;
import static org.netmelody.cieye.core.domain.Status.UNDER_INVESTIGATION;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDigest;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.core.observation.ObservationAgency;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class MyPluginObservationAgencyTest {

	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	private final CommunicationNetwork unusedNetwork = null;
	private final KnownOffendersDirectory unusedDirectory = null;
	
	@Test public void canProvideSpyForPluginDemo() throws Exception {
		ObservationAgency agency = new MyPluginObservationAgency();
		
		assertTrue(agency.canProvideSpyFor(new CiServerType("PLUGIN_DEMO")));
		assertFalse(agency.canProvideSpyFor(new CiServerType("SOMETHING_ELSE")));
	}
	
	@Test public void canReturnStatusOfFiles() throws Exception {
		createStatus("tom", "All Good");
		createStatus("dick", "Uh-oh");
		createStatus("harry", "Checking");

		ObservationAgency agency = new MyPluginObservationAgency();
		CiSpy spy = agency.provideSpyFor(new Feature("whatever", "anything", new CiServerType("PLUGIN_DEMO")), unusedNetwork, unusedDirectory);
		
		String endpoint = folder.getRoot().getAbsolutePath();
		
		TargetDetail tomTargetDetail = targetFor("tom", spy, endpoint);
		TargetDetail dickTargetDetail = targetFor("dick", spy, endpoint);
		TargetDetail harryTargetDetail = targetFor("harry", spy, endpoint);
		
		assertThat(tomTargetDetail.name(), is("tom"));
		assertThat(tomTargetDetail.status(), is(GREEN));
		assertThat(dickTargetDetail.name(), is("dick"));
		assertThat(dickTargetDetail.status(), is(BROKEN));
		assertThat(harryTargetDetail.name(), is("harry"));
		assertThat(harryTargetDetail.status(), is(UNDER_INVESTIGATION));
	}
	
	@Test public void returnsStatusOfHardcodedInternalCiServer()  throws Exception {
		ObservationAgency agency = new MyPluginObservationAgency();
		CiSpy spy = agency.provideSpyFor(new Feature("whatever", "anything", new CiServerType("PLUGIN_DEMO")), unusedNetwork, unusedDirectory);
		
		String endpoint = new File("internal_ci_server").getAbsolutePath();
		
		TargetDetail georgeTargetDetail = targetFor("george", spy, endpoint);
		TargetDetail johnTargetDetail = targetFor("john", spy, endpoint);
		TargetDetail paulTargetDetail = targetFor("paul", spy, endpoint);
		TargetDetail ringoTargetDetail = targetFor("ringo", spy, endpoint);
		
		assertThat(georgeTargetDetail.name(), is("george"));
		assertThat(georgeTargetDetail.status(), is(GREEN));
		assertThat(johnTargetDetail.name(), is("john"));
		assertThat(johnTargetDetail.status(), is(UNDER_INVESTIGATION));
		assertThat(paulTargetDetail.name(), is("paul"));
		assertThat(paulTargetDetail.status(), is(BROKEN));
		assertThat(ringoTargetDetail.name(), is("ringo"));
		assertThat(ringoTargetDetail.status(), is(GREEN));
	}
	
	@Test public void canChangeStatusToUnderInvestigationByTakingNote() throws Exception {
		createStatus("broken", "Uh-oh");

		ObservationAgency agency = new MyPluginObservationAgency();
		CiSpy spy = agency.provideSpyFor(new Feature("whatever", "anything", new CiServerType("PLUGIN_DEMO")), unusedNetwork, unusedDirectory);
		
		String endpoint = folder.getRoot().getAbsolutePath();
		
		TargetDetail brokenTargetDetail = targetFor("broken", spy, endpoint);
		
		assertThat(brokenTargetDetail.name(), is("broken"));
		assertThat(brokenTargetDetail.status(), is(BROKEN));
		
		spy.takeNoteOf(brokenTargetDetail.id(), "anything");
		
		TargetDetail investigatingTargetDetail = targetFor("broken", spy, endpoint);
		
		assertThat(investigatingTargetDetail.name(), is("broken"));
		assertThat(investigatingTargetDetail.status(), is(UNDER_INVESTIGATION));
		assertThat(readLines(new File(endpoint, "broken"), Charsets.UTF_8), contains("Checking", "anything"));
	}

	private TargetDetail targetFor(String featureName, CiSpy spy, String endpoint) {
		Feature tomFeature = new Feature(featureName, endpoint, new CiServerType("PLUGIN_DEMO"));
		TargetDigestGroup targetsConstituting = spy.targetsConstituting(tomFeature);
		TargetDigest target = find(targetsConstituting, alwaysTrue());
		return spy.statusOf(target.id());
	}

	private void createStatus(String featureName, String currentStatus) throws IOException {
		File jobStatus = folder.newFile(featureName);
		Files.write(currentStatus, jobStatus, Charsets.UTF_8);
	} 
}
