package com.moogu.myweb.client.bootstrap;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.moogu.myweb.client.bootstrap.AsyncJobSequencer.JobEvent;
import com.moogu.myweb.client.feature.MainPanel;

/**
 * Responsible for displaying the splash screen and animate the progress bar.
 */
public class SplachScreenDisplayer {

	public static void display(AsyncJobSequencer sequencer) {

		final MessageBox box = MessageBox.progress("Please wait",
				"Loading application ...", "Initializing...");
		box.getDialog().setWidth(400);
		final ProgressBar bar = box.getProgressBar();
		final int totalJobCount = sequencer.getItemCount();

		sequencer
				.addJobStartedListener(new Listener<AsyncJobSequencer.JobEvent>() {

					public void handleEvent(final JobEvent be) {

						final AsyncJob<?> job = be.job;
						final String jobName = job.getName();

						final float percent = ((be.job.getOrder()) * 100)
								/ totalJobCount;
						final float value = percent / 100;
						bar.updateProgress(value, jobName + " ...");
					}
				});

		sequencer
				.addJobSuccessListener(new Listener<AsyncJobSequencer.JobEvent>() {

					public void handleEvent(JobEvent be) {

						final AsyncJob<?> job = be.job;
						final String jobName = job.getName();
						final float percent = ((be.job.getOrder() + 1) * 100)
								/ totalJobCount;
						final float value = percent / 100;
						bar.updateProgress(value, jobName + " done");

						// If no more job to be done
						if (be.job.getOrder() + 1 == totalJobCount) {
							bar.updateProgress(1, "Rendering elements ...");
							RootPanel.get("MYWEB_ROOT_PANEL").add(
									MainPanel.getInstance());
							final Timer timer = new Timer() {

								@Override
								public void run() {
									box.close();

								}
							};
							timer.schedule(750);

						}
					}
				});
		sequencer.processAll();
	}
}