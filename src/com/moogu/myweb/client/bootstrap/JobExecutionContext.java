package com.moogu.myweb.client.bootstrap;

/**
 * Data needed to be shared between jobs for the application initialization
 * step.
 */
public class JobExecutionContext {

	private Integer userId;

	public Integer getUserId() {
		if (userId == null) {
			throw new IllegalStateException("User Id not found");
		}

		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

}
