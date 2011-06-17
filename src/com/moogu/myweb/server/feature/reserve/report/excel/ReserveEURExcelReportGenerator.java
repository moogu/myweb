package com.moogu.myweb.server.feature.reserve.report.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.springframework.stereotype.Component;

import com.moogu.myweb.server.ReserveModuleRemoteServiceImpl;
import com.moogu.myweb.server.utils.DateHelper;
import com.moogu.myweb.shared.reserve.ReserveEurEoDBalance;

@Component
public class ReserveEURExcelReportGenerator {

    public void generateReport(OutputStream outputStream, HttpServletRequest request) {
        try {
            this._generateReport(outputStream, request);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void _generateReport(OutputStream outputStream, HttpServletRequest httpServletRequest) throws Exception {
        // Getting data
        final HttpSession httpSession = httpServletRequest.getSession();
        final ReserveEURExcelReport report = (ReserveEURExcelReport) httpSession.getAttribute(ReserveModuleRemoteServiceImpl.LAST_RESERVE_EUR_REPORT_DETAILS);
        final WritableWorkbook workbook = this.createWorkbook(outputStream);
        final WritableSheet sheet = workbook.createSheet("Reserve EUR", 0);
        // getting format 
        final FormatInfo formatInfo = new FormatInfo();

        if (report != null) {
            // set date
            Label dateLabel = new Label(
                            0,
                            0,
                            report.header,
                            formatInfo.titleFormat);
            sheet.addCell(dateLabel);

            // when it was generated
            dateLabel = new Label(0, 1, "Generated", formatInfo.titleFormat);
            sheet.addCell(dateLabel);
            dateLabel = new Label(1, 1, DateHelper.formatToDateTime(new Date()), formatInfo.titleFormat);
            sheet.addCell(dateLabel);

            dateLabel = new Label(0, 2, "Mandatory average", formatInfo.titleFormat);
            sheet.addCell(dateLabel);
            final Number amount = new Number(1,
                            2,
                            report.mandatoryAverage,
                            formatInfo.numberFormat);
            sheet.addCell(amount);

            // headers
            if (report.balances != null) {
                // to generate the Header
                this.generateHeader(sheet, formatInfo);

                // create regular rows
                this.populateRows(report.balances, sheet, 6, formatInfo);
            } else {
                dateLabel = new Label(0, 3, "No data");
                sheet.addCell(dateLabel);
            }
        } else {
            Label dateLabel = new Label(0, 0, "Generated", formatInfo.titleFormat);
            sheet.addCell(dateLabel);
            dateLabel = new Label(1, 0, DateHelper.formatToDateTime(new Date()), formatInfo.titleFormat);
            sheet.addCell(dateLabel);

            dateLabel = new Label(0, 2, "Mandatory average", formatInfo.titleFormat);
            sheet.addCell(dateLabel);
            dateLabel = new Label(1, 2, "0", formatInfo.titleFormat);
            sheet.addCell(dateLabel);

            dateLabel = new Label(0, 3, "No data");
            sheet.addCell(dateLabel);
        }

        // write and close and finish.
        workbook.write();
        workbook.close();
        return;

    }

    private void generateHeader(final WritableSheet sheet, FormatInfo formatInfo) throws WriteException,
                    RowsExceededException {
        Label dateLabel = new Label(0, 5, "Date", formatInfo.headerTableFormat);
        sheet.addCell(dateLabel);

        dateLabel = new Label(1, 5, "Average to realize", formatInfo.headerTableFormat);
        sheet.addCell(dateLabel);

        dateLabel = new Label(2, 5, "EoD Balance", formatInfo.headerTableFormat);
        sheet.addCell(dateLabel);

        dateLabel = new Label(3, 5, "D/L", formatInfo.headerTableFormat);
        sheet.addCell(dateLabel);

        dateLabel = new Label(4, 5, "D/L Amount", formatInfo.headerTableFormat);
        sheet.addCell(dateLabel);
    }

    private void populateRows(
                    List<ReserveEurEoDBalance> balances,
                    WritableSheet sheet,
                    int rowIndex,
                    FormatInfo formatInfo) throws WriteException, RowsExceededException {

        Label regularLabel;

        for (final ReserveEurEoDBalance balance : balances) {

            regularLabel = new Label(0, rowIndex, DateHelper.formatToDate(balance.getDatePublish()));
            sheet.addCell(regularLabel);

            if (balance.getAverage() != null) {
                final Number amount = new Number(1,
                                rowIndex,
                                balance.getAverage(),
                                formatInfo.numberFormat);
                sheet.addCell(amount);
            }

            if (balance.getBalance() != null) {
                final Number amount = new Number(2,
                                rowIndex,
                                balance.getBalance(),
                                formatInfo.numberFormat);
                sheet.addCell(amount);
            }

            if (balance.getDeposit() != null) {
                // check for debit    
                regularLabel = new Label(3, rowIndex, "D");
                sheet.addCell(regularLabel);

                final Number amount = new Number(4,
                                rowIndex,
                                balance.getDeposit(),
                                formatInfo.numberFormat);
                sheet.addCell(amount);

            } else if (balance.getMarginalLending() != null) {
                // check for lending  
                regularLabel = new Label(3, rowIndex, "L");
                sheet.addCell(regularLabel);

                final Number amount = new Number(4,
                                rowIndex,
                                balance.getMarginalLending(),
                                formatInfo.numberFormat);
                sheet.addCell(amount);
            }

            rowIndex++;
        }
    }

    private WritableWorkbook createWorkbook(OutputStream outputStream) throws IOException {
        final WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(Locale.UK);
        final WritableWorkbook wwb = Workbook.createWorkbook(outputStream, wbSettings);
        return wwb;
    }

    private static class FormatInfo {

        final WritableCellFormat numberFormat;

        final WritableCellFormat titleFormat;

        final WritableCellFormat headerTableFormat;

        public FormatInfo() {
            final WritableFont bold = new WritableFont(
                            WritableFont.ARIAL,
                            WritableFont.DEFAULT_POINT_SIZE,
                            WritableFont.BOLD);

            final WritableFont boldItalic = new WritableFont(
                            WritableFont.ARIAL,
                            WritableFont.DEFAULT_POINT_SIZE,
                            WritableFont.BOLD, true, UnderlineStyle.NO_UNDERLINE, Colour.BLUE);

            this.numberFormat = new WritableCellFormat(NumberFormats.FORMAT3);

            this.titleFormat = new WritableCellFormat(bold);

            this.headerTableFormat = new WritableCellFormat(boldItalic);
            try {
                this.headerTableFormat.setAlignment(Alignment.CENTRE);
            } catch (final WriteException e) {
                throw new RuntimeException(e);
            }
        }
    }
}