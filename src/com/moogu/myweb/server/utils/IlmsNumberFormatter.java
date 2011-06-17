package com.moogu.myweb.server.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.moogu.myweb.shared.utils.IlmsFormats;

public class IlmsNumberFormatter {

    public final static DecimalFormat POSITION_DECIMAL_FORMAT = new DecimalFormat(IlmsFormats.POSITION_FORMAT);

    public static String reformat(String amount) {

        /** default format, to be added the decimal information */
        String format = "#,###,###,##0";
        /** if currency does not exit OR was not filled, return in the format .00" */
        format += ".00";

        final NumberFormat numberFormat = new DecimalFormat(format);
        final Double doubleValue = Double.parseDouble(amount);
        return numberFormat.format(doubleValue);
    }
}
