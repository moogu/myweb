package com.moogu.myweb.shared.utils;

import com.google.gwt.i18n.client.NumberFormat;

/**
 * We only keep here the string pattern, has it should be reusable both on client and server side.
 * 
 * @author i19451
 */
public class IlmsFormats {

    public static final String POSITION_FORMAT = "#,###,###,##0.00";

    public static final String DATE_FORMAT = "dd/MM/yyyy";

    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static final String DATE_TIME_MILIS_FORMAT = "dd/MM/yyyy HH:mm:ss.SSS";

    public static final String HOUR_FORMAT = "HH:mm:ss";

    public static final String ISO8601_FORMAT = "yyyy-MM-dd";

    /**
     * Beware, use only this method at client side
     */
    public static final NumberFormat POSITION_NUMBER_FORMAT = NumberFormat.getFormat(IlmsFormats.POSITION_FORMAT);

    public static final String EXCEL_FORMAT = "excel";

    public static final String PDF_FORMAT = "pdf";
}
