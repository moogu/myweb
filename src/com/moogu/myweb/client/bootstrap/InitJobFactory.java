package com.moogu.myweb.client.bootstrap;

import java.util.Map;

import com.extjs.gxt.ui.client.widget.Info;
import com.moogu.myweb.client.MyWebEntryPoint;
import com.moogu.myweb.client.RemoteServicePool;
import com.moogu.myweb.client.StaticDataStorage;
import com.moogu.myweb.shared.management.IlmsConfigConstants;

/**
 * Defines and creates all the jobs needed to to launch the application.
 * 
 */
public final class InitJobFactory {

	public final static AsyncJob<?> LOAD_USER_INFO = new AsyncJob<Map<String, String>>() {

		@Override
		public String getName() {
			return "Loading User Informations";
		}

		@Override
		public void onSuccess(JobExecutionContext context,
				Map<String, String> result) {
			MyWebEntryPoint.setUserInfo(result);
			context.setUserId(MyWebEntryPoint.getUserIdAsInteger());
		}

		@Override
		public void run(JobExecutionContext context,
				JobAsyncCallback<Map<String, String>> callback) {
			RemoteServicePool.getLoginModule().getUserInfo(callback);
		}

	};

	public final static AsyncJob<?> LOAD_DEFAULT_PROPERTIES = new AsyncJob<Map<String, String>>() {

		@Override
		public String getName() {
			return "Loading default_properties";
		}

		@Override
		public void onSuccess(JobExecutionContext context,
				Map<String, String> result) {

			final String reserEurRefreshValue = result
					.get(IlmsConfigConstants.RESERVE_EUR_OVERVIEW_SCREEN_REFRESH_SECONDS);
			try {
				if (reserEurRefreshValue != null) {
					final Integer sec = Integer.parseInt(reserEurRefreshValue);
					StaticDataStorage.reserveEurRefreshRateSeconds = sec;
				}
			} catch (final NumberFormatException e) {
				Info.display("Warning",
						"Reserve Eur refresh time has been set as "
								+ reserEurRefreshValue
								+ " (not parseable to an integer). Use default");
			}
		}

		@Override
		public void run(JobExecutionContext context,
				JobAsyncCallback<Map<String, String>> callback) {
			RemoteServicePool.getCommonModule().getProperties(callback);

		}

	};

	public static AsyncJobSequencer createInitJobSequencer() {
		final AsyncJobSequencer result = new AsyncJobSequencer();
		result.add(InitJobFactory.LOAD_USER_INFO);
		result.add(InitJobFactory.LOAD_DEFAULT_PROPERTIES);
		return result;
	}
}