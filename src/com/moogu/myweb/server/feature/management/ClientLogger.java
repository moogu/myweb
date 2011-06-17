package com.moogu.myweb.server.feature.management;

import org.apache.log4j.Logger;

import com.moogu.myweb.client.common.ClientLog;

/**
 * Just thin wrapper around logger
 */
public class ClientLogger {

    private final static Logger logger = Logger.getLogger(ClientLogger.class.getName());

    public static void log(ClientLog.Type log, String message) {

        /** log information is DEBUG is enabled only */
        if (log == ClientLog.Type.DEBUG) {
            if (ClientLogger.logger.isDebugEnabled()) {
                ClientLogger.logger.debug(message);
            }
        } else if (log == ClientLog.Type.WARN) {
            ClientLogger.logger.warn(message);
        } else if (log == ClientLog.Type.INFO) {
            /** log information if INFO is enabled */
            if (ClientLogger.logger.isInfoEnabled()) {
                ClientLogger.logger.info(message);
            }
        } else if (log == ClientLog.Type.AUDIT) {
            /** log information if INFO is enabled */
            if (ClientLogger.logger.isInfoEnabled()) {
                ClientLogger.logger.info("AUDIT: " + message);
            }
        } else if (log == ClientLog.Type.ERROR) {
            /** error is always enabled */
            ClientLogger.logger.error(message);
        }
    }
}