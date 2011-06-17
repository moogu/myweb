package com.moogu.myweb.client.bootstrap;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.BaseObservable;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * Responsible to process in sequence a list of job. At the end of each job, an
 * event is fired.
 */
public class AsyncJobSequencer {

	public static final class JobEvent extends BaseEvent {

		public AsyncJob<?> job;

		public JobEvent(EventType eventType, AsyncJob<?> job) {
			super(eventType);
			this.job = job;
		}

	}

	static final EventType jobStarted = new EventType();

	static final EventType jobSuccess = new EventType();

	static final EventType jobFailure = new EventType();

	private final BaseObservable observable = new BaseObservable();

	private final List<AsyncJob<?>> initJobs = new ArrayList<AsyncJob<?>>();

	public void add(AsyncJob<?> initJob) {
		initJobs.add(initJob);
	}

	public final void addJobFailureListener(Listener<JobEvent> listener) {
		observable.addListener(AsyncJobSequencer.jobFailure, listener);
	}

	public final void addJobStartedListener(Listener<JobEvent> listener) {
		observable.addListener(AsyncJobSequencer.jobStarted, listener);
	}

	public final void addJobSuccessListener(Listener<JobEvent> listener) {
		observable.addListener(AsyncJobSequencer.jobSuccess, listener);
	}

	public int getItemCount() {
		return initJobs.size();
	}

	public void processAll() {
		for (int i = 0; i < initJobs.size() - 1; i++) {
			initJobs.get(i).setNextInitJob(initJobs.get(i + 1));
			initJobs.get(i).setOrder(i);
		}
		initJobs.get(initJobs.size() - 1).setOrder(initJobs.size() - 1);
		final JobExecutionContext executionContext = new JobExecutionContext();
		initJobs.get(0).runAndLaunchNextOnSuccess(executionContext, observable);
	}
}