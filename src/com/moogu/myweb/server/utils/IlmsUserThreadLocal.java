package com.moogu.myweb.server.utils;

/**
 * Save information from a specific thread
 * 
 * @author i21726 - (Patrick Santana)
 * 
 */
public class IlmsUserThreadLocal {

    private static ThreadLocal<String> user = new ThreadLocal<String>();

    /**
     * Remove the variable
     */
    public static void cleanUser() {
        IlmsUserThreadLocal.user.remove();
    }

    /**
     * Get the user from this thread local
     * 
     * @return the user
     */
    public static String getUser() {
        return IlmsUserThreadLocal.user.get();
    }

    /**
     * Set the user for this thread local
     * 
     * @param pUser the user
     */
    public static void setUser(String pUser) {
        IlmsUserThreadLocal.user.set(pUser);
    }
}
