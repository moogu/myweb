package com.moogu.myweb.shared.utils;

/**
 * Class with all shared exceptions codes
 * 
 * @author i21726 - Patrick Santana
 * 
 */
public class IlmsSharedExceptionCodes {

    /** it means that user is trying to save a period where the END DATE is before START DATE */
    public final static String END_DATE_BEFORE_START_DATE = "END_DATE_BEFORE_START_DATE";

    /** user tries to create a period with a start date already used */
    public final static String START_DATE_NOT_AVAILABLE = "START_DATE_NOT_AVAILABLE";

    /** publish date and balance are mandatory */
    public static final String PUBLISH_DATE_AND_BALANCE_SHOULD_NOT_BE_NULL = "PUBLISH_DATE_AND_BALANCE_SHOULD_NOT_BE_NULL";

    /** user can not choose a start date that is already being used */
    public static final String START_DATE_ALREADY_IN_ANOTHER_PERIOD = "START_DATE_ALREADY_IN_ANOTHER_PERIOD";

    /** user can not choose a e,d date that is already being used */
    public static final String END_DATE_ALREADY_IN_ANOTHER_PERIOD = "END_DATE_ALREADY_IN_ANOTHER_PERIOD";
}
