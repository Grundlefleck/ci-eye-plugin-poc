package com.github.grundlefleck.cieye;

import java.util.Collections;
import java.util.Map;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDigest;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetId;
import org.netmelody.cieye.core.observation.CiSpy;

import com.google.common.collect.Maps;

class PluginDemoSpy implements CiSpy {
	
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