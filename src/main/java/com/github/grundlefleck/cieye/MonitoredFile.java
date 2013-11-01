package com.github.grundlefleck.cieye;

import static org.netmelody.cieye.core.domain.Status.UNDER_INVESTIGATION;

import java.io.File;
import java.io.IOException;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.logging.LogKeeper;

import com.google.common.base.Charsets;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.io.Files;

class MonitoredFile {
	private static final BiMap<String, Status> internalToCiEye = ImmutableBiMap.of(
			"All Good", Status.GREEN, 
			"Uh-oh", Status.BROKEN,
			"Checking", Status.UNDER_INVESTIGATION);
	
	private final File file;
	final String name;

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
			String withNote = String.format("%s%n%s%n", internalToCiEye.inverse().get(UNDER_INVESTIGATION), note);
			Files.write(withNote, file, Charsets.UTF_8);
		} catch (IOException e) {
			LogKeeper.logbookFor(getClass()).error("Couldn't add note", e);
		}
	}
}