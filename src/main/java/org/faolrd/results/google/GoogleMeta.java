package org.faolrd.results.google;

import org.faolrd.results.Meta;

/**
 * 
 * @author jnphilipp
 * @version 0.0.2
 */
public class GoogleMeta extends Meta {
	private int estimatedCount;

	public GoogleMeta() {}

	public GoogleMeta(String url) {
		super(url);
	}

	public GoogleMeta(String url, int estimatedCount) {
		super(url);
		this.estimatedCount = estimatedCount;
	}

	public GoogleMeta(String url, int estimatedCount, int count) {
		super(url, count);
		this.estimatedCount = estimatedCount;
	}

	public int getEstimatedCount() {
		return this.estimatedCount;
	}

	public void setEstimatedCount(int estimatedCount) {
		this.estimatedCount = estimatedCount;
	}
}