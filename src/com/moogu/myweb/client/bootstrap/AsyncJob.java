package com.moogu.myweb.client.bootstrap;

import com.extjs.gxt.ui.client.event.Observable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.moogu.myweb.client.bootstrap.AsyncJobSequencer.JobEvent;
import com.moogu.myweb.client.common.widget.dialog.SharedExceptionDialog;
import com.moogu.myweb.shared.utils.IlmsSharedException;

/**
 * Processing object for the chain of responsibility. Each {@link AsyncJob}
 * should implement two methods, one to perform the asynchronous remote call,
 * the other to handle the return of this call.</p>
 * 
 * The {@link AsyncJobSequencer} will be responsible link each job to each
 * other, constituting a chain of responsibility
 * 
 * @param <T>
 *            The result type returned by the async callback.
 */
public abstract class AsyncJob<T> {

	static class JobAsyncCallback<T> implements AsyncCallback<T> {

		private final AsyncJob<T> current;

		private final AsyncJob<?> next;

		private final Observable observable;

		private final JobExecutionContext executionContext;

		public JobAsyncCallback(JobExecutionContext jobExecutionContext,
				AsyncJob<T> current, AsyncJob<?> next,
				Observable asyncJobSequencer) {
			super();
			this.current = current;
			this.next = next;
			this.observable = asyncJobSequencer;
			this.executionContext = jobExecutionContext;
		}

		public final void onFailure(Throwable throwable) {
			if (throwable instanceof IlmsSharedException) {
				final IlmsSharedException exception = (IlmsSharedException) throwable;
				SharedExceptionDialog.display(
						"Error while initialitizing application",
						exception.getStringStackTrace());
			} else {
				SharedExceptionDialog.display(
						"Error while initialitizing application",
						throwable.getMessage());
			}
			this.observable.fireEvent(AsyncJobSequencer.jobFailure,
					new JobEvent(AsyncJobSequencer.jobFailure, this.current));

		}

		public final void onSuccess(T result) {
			this.current.onSuccess(this.executionContext, result);
			this.observable.fireEvent(AsyncJobSequencer.jobSuccess,
					new JobEvent(AsyncJobSequencer.jobSuccess, this.current));
			if (this.next != null) {
				this.next.runAndLaunchNextOnSuccess(this.executionContext,
						this.observable);
			}
		}

	}

	private AsyncJob<?> next;

	private int order;

	public abstract String getName();

	final AsyncJob<?> getNext() {
		return this.next;
	}

	final int getOrder() {
		return this.order;
	}

	public abstract void onSuccess(JobExecutionContext context, T result);

	public abstract void run(JobExecutionContext context,
			JobAsyncCallback<T> callback);

	final void runAndLaunchNextOnSuccess(JobExecutionContext executionContext,
			Observable observable) {
		final JobAsyncCallback<T> callback = new JobAsyncCallback<T>(
				executionContext, this, this.next, observable);
		observable.fireEvent(AsyncJobSequencer.jobStarted, new JobEvent(
				AsyncJobSequencer.jobStarted, this));
		this.run(executionContext, callback);
	}

	final void setNextInitJob(AsyncJob<?> initJob) {
		this.next = initJob;
	}

	final void setOrder(int order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "Job " + this.getName();
	}
}