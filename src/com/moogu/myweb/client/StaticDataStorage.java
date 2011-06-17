package com.moogu.myweb.client;

import java.util.Date;

/**
 * This class offer access to pre-load static data each time a connection occurs.
 * 
 * Keep in mind that this class will finally turn into Javascript code. So public static fields are perfectly acceptable
 * as it will just turn into a Javascript global variable.
 * 
 * @author i03752
 * 
 */
public final class StaticDataStorage {

    public static Integer reserveEurRefreshRateSeconds;

    public final static StaticDataStorage staticDataStorage = new StaticDataStorage();

    /**
     * Return the unique instance of <code>StaticDataStorage</code>
     */
    public static StaticDataStorage getInstance() {
        return StaticDataStorage.staticDataStorage;
    }

    private final Date initializationDate = new Date();

    /**
     * Constructs and initialise the instance
     */
    private StaticDataStorage() {
        super();
    }

    /**
     * Get the date/time that the user started the application
     * 
     * @return the date
     */
    public Date getInitializationDate() {
        return initializationDate;
    }
}