package org.faolrd.results;

public abstract class Result {
	private String content;
	private String title;
	private String url;

	public Result() {}

	public Result(String title, String url) {
		this.title = title;
		this.url = url;
	}

	public Result(String title, String url, String content) {
		this.content = content;
		this.title = title;
		this.url = url;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}