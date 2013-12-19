package org.faolrd.results.google;

import org.faolrd.results.Meta;

public class GoogleMeta extends Meta {
	private int estimatedCount;

	public GoogleMeta() {}

	public GoogleMeta(String url) {
		super(url);
	}

	public GoogleMeta(String url, int estimatedCount) {
		super(url);
		this.setEstimatedCount(estimatedCount);
	}

	public GoogleMeta(String url, int estimatedCount, int count) {
		super(url, count);
		this.setEstimatedCount(estimatedCount);
	}

	public int getEstimatedCount() {
		return this.estimatedCount;
	}

	public void setEstimatedCount(int estimatedCount) {
		this.estimatedCount = estimatedCount;
	}
}