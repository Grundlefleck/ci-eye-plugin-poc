package com.github.grundlefleck.cieye;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDigest;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetId;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.core.observation.ObservationAgency;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class MyPluginObservationAgency implements ObservationAgency {

	@Override
	public boolean canProvideSpyFor(CiServerType type) {
		return "PLUGIN_DEMO".equals(type.name());
	}

	@Override
	public CiSpy provideSpyFor(Feature feature, CommunicationNetwork network, KnownOffendersDirectory directory) {
		return new PluginDemoSpy();
	}

	static class PluginDemoSpy implements CiSpy {
		
		private final Map<TargetId, MonitoredFile> monitoredFiles = Maps.newHashMap();

		@Override
		public TargetDigestGroup targetsConstituting(Feature feature) {
			TargetDigest target = new TargetDigest(feature.name(), feature.endpoint(), feature.name(), Status.UNKNOWN);
			monitoredFiles.put(target.id(), new MonitoredFile(feature));
			return new TargetDigestGroup(Collections.singleton(target));
		}

		@Override
		public TargetDetail statusOf(TargetId target) {
			final MonitoredFile monitoredFile = monitoredFiles.get(target);
	        if (null == monitoredFile) {
	            return null;
	        }
			return detailsOf(target, monitoredFile);
		}

		@Override
		public boolean takeNoteOf(TargetId target, String note) {
			final MonitoredFile monitoredFile = monitoredFiles.get(target);
	        if (null == monitoredFile) {
	            return false;
	        }
	        monitoredFile.addNote(note);
	        return true;
		}
		
		public TargetDetail detailsOf(TargetId target, MonitoredFile monitoredFile) {
			String webUrl = "www.example.com";
			return new TargetDetail(target.id(), webUrl, monitoredFile.name, monitoredFile.status(), monitoredFile.timestamp());
		}
	}
	
	static class MonitoredFile {
		private static final Map<String, Status> internalToCiEye = ImmutableMap.of(
				"All Good", Status.GREEN, 
				"Uh-oh", Status.BROKEN,
				"Checking", Status.UNDER_INVESTIGATION);
		
		private final File file;
		private final String name;

		public MonitoredFile(Feature feature) {
			this.name = feature.name();
			this.file = new File(feature.endpoint(), feature.name());
		}
		
		public Status status() {
			try {
				String status = Files.readFirstLine(file, Charsets.UTF_8);
				return internalToCiEye.get(status);
			} catch (IOException e) {
				LogKeeper.logbookFor(getClass()).error("Couldn't find status", e);
				return Status.UNKNOWN;
			}
		}
		
		public long timestamp() {
			return file.lastModified();
		}
		
		public void addNote(String note) {
			try {
				String withNote = String.format("%s%n%s%n", "Checking", note);
				Files.write(withNote, file, Charsets.UTF_8);
			} catch (IOException e) {
				LogKeeper.logbookFor(getClass()).error("Couldn't add note", e);
			}
		}
	}
	

	public static void main(String[] args) {
		ObservationAgency agency = new MyPluginObservationAgency();
		
		agency.canProvideSpyFor(new CiServerType("PLUGIN_DEMO"));
	}

}
