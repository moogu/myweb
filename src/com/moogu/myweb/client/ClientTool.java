package com.moogu.myweb.client;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * A utility class for common purpose about client (visual) issue in the app
 * 
 * @author i03752
 * 
 */
public class ClientTool {

    /** format for number in the application */
    public static final String NUMBER_FORMAT = "#,###,###,##0.00";

    public static final NumberFormat POSITION_FORMATER = NumberFormat.getFormat(ClientTool.NUMBER_FORMAT);

    public static final DateTimeFormat FULL_DATE_TIME_FORMAT = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss");

    public static final DateTimeFormat TIME_ONLY_FORMAT = DateTimeFormat.getFormat("HH:mm:ss");

    private static final DateTimeFormat DAY_ONLY_FORMAT = DateTimeFormat.getFormat("dd/MM/yyyy");

    public static final DateTimeFormat MILLIS_DATE_TIME_FORMAT = DateTimeFormat.getFormat("dd/MM HH:mm:ss.SSS");

    public static final String EURO_HTML = "&euro;";

    /**
     * Find the next relevant date to auto feed value dates fields (TN and SN)
     * 
     * @param startDate
     * @return
     */

    @SuppressWarnings({ "deprecation" })
    public static Date calculateNextDate(Date startDate) {
        final Date rolledDate = new Date(startDate.getTime());
        while (!rolledDate.after(startDate) || (rolledDate.getDay() == 6 || rolledDate.getDay() == 0)) {
            rolledDate.setTime(rolledDate.getTime() + (24 * 60 * 60 * 1000)); // Get 1 day more
        }
        return rolledDate;
    }

    public static Date calculatePreviousDate(Date startDate) {
        return new Date(startDate.getTime() - (24 * 60 * 60 * 1000));
    }

    /**
     * Get the message and apply bold and blue color to it
     * 
     * @param text the text that you want to change
     * @return message with changes in html
     */
    public static String getBlueBoldColorHTML(String text) {
        return "<b><font color=#15428b>" + text + "</font></b>";
    }

    /**
     * Get the message and apply blue color to it
     * 
     * @param text the text that you want to change
     * @return message with changes in html
     */
    public static String getBlueColorHTML(String text) {
        return "<font color=#15428b>" + text + "</font>";
    }

    /**
     * Get the message and apply bold a to it
     * 
     * @param text the text that you want to change
     * @return message with changes in html
     */
    public static String getBoldColorHTML(String text) {
        return "<b>" + text + "</b>";
    }

    /**
     * Get the message and apply bold and Green color to it
     * 
     * @param text the text that you want to change
     * @return message with changes in html
     */
    public static String getGreenBoldColorHTML(String text) {
        return "<b><font color=green>" + text + "</font></b>";
    }

    /**
     * Get the message and apply Green color to it
     * 
     * @param text the text that you want to change
     * @return message with changes in html
     */
    public static String getGreenColorHTML(String text) {
        return "<font color=green>" + text + "</font>";
    }

    /**
     * Get the message and apply bold and red color to it
     * 
     * @param text the text that you want to change
     * @return message with changes in html
     */
    public static String getRedBoldColorHTML(String text) {
        return "<b><font color=#FF0000>" + text + "</font></b>";
    }

    /**
     * Get the message and apply red color to it
     * 
     * @param text the text that you want to change
     * @return message with changes in html
     */
    public static String getRedColorHTML(String text) {
        return "<font color=#FF0000>" + text + "</font>";
    }

    public static boolean isNullOrEmpty(String stringToTest) {
        if (stringToTest == null) {
            return true;
        }
        if (stringToTest.trim().length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        final String dateString1 = ClientTool.DAY_ONLY_FORMAT.format(date1);
        final String dateString2 = ClientTool.DAY_ONLY_FORMAT.format(date2);
        return dateString1.equals(dateString2);
    }

    @SuppressWarnings("rawtypes")
    public static void resizeColumns(final Store gridStore,
                    final ColumnModel gridColsModel,
                    final ColumnModel gridColsToCompare1,
                    final ColumnModel gridColsToCompare2) {
        if (gridStore == null || gridColsModel == null) {
            return;
        }
        final List<ColumnConfig> colCfgs = gridColsModel.getColumns();
        if (colCfgs == null) {
            return;
        }
        final int nbRows = gridStore.getModels().size();
        if (nbRows > 0) {
            final List records = gridStore.getModels();
            /* Resize each column according the cell values */
            for (int i = 0; i < colCfgs.size(); i++) {
                String strHeader = colCfgs.get(i).getHeader();
                // In case of HTML header
                if (strHeader.startsWith("<") && strHeader.contains(">")) {
                    if (!strHeader.contains("<img")) {
                        strHeader = strHeader.substring(strHeader.indexOf('>'), strHeader.lastIndexOf('<'));
                    }
                }
                int colW = strHeader.length();
                if (strHeader.contains("<img")) {
                    colW = 30;
                }
                for (int r = 0; r < nbRows; r++) {
                    if (records.get(r) != null) {
                        Object val = ((ModelData) records.get(r)).get(colCfgs.get(i).getId());
                        if (val != null) {
                            if (val instanceof Double) {
                                val = NumberFormat.getDecimalFormat().format((Double) val);
                            } else {
                                if (!(val.toString().startsWith("<") && val.toString().contains(">"))) {
                                    val = val.toString();
                                } else {
                                    val = "";
                                }
                            }
                            if (((String) val).length() > colW) {
                                colW = ((String) val).length();
                            }
                        }
                    }
                }

                /** final size. We will compare with the others columns. If user gives null as parameter, do nothing */
                int finalColumnSize = 6 * colW + 30;

                /** check the size of others columns */
                if (gridColsToCompare1 != null) {
                    final int temp = gridColsToCompare1.getColumnWidth(i);

                    if (temp > finalColumnSize) {
                        finalColumnSize = temp;
                    } else {
                        gridColsToCompare1.setColumnWidth(i, finalColumnSize);
                    }
                }

                /** check the size of others columns */
                if (gridColsToCompare2 != null) {
                    final int temp = gridColsToCompare2.getColumnWidth(i);

                    if (temp > finalColumnSize) {
                        finalColumnSize = temp;
                    } else {
                        gridColsToCompare2.setColumnWidth(i, finalColumnSize);
                    }
                }

                gridColsModel.setColumnWidth(i, finalColumnSize);
            }
        }
    }
}