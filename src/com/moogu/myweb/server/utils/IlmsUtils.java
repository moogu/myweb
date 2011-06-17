package com.moogu.myweb.server.utils;

public class IlmsUtils {

    /** system variable */
    public final static String SYSTEM_VARIABLE_ENVIRONMENT = "com.fortis.environment";

    /**
     * Return the environment that the server is connect
     * 
     * @return
     */
    public static String getEnvironment() {
        final String env = System.getProperty(IlmsUtils.SYSTEM_VARIABLE_ENVIRONMENT);

        /** local development needs to return DEV */
        if (env == null) {
            return "DEV";
        }

        return env;
    }
}
