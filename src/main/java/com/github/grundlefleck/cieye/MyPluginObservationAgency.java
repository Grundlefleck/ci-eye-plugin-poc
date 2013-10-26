package com.github.grundlefleck.cieye;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.core.observation.ObservationAgency;

public class MyPluginObservationAgency implements ObservationAgency {

	@Override
	public boolean canProvideSpyFor(CiServerType type) {
		return "PLUGIN_DEMO".equals(type.name());
	}

	@Override
	public CiSpy provideSpyFor(Feature feature, CommunicationNetwork network, KnownOffendersDirectory directory) {
		return new PluginDemoSpy();
	}

}
