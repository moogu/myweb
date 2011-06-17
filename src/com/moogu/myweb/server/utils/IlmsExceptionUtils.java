package com.moogu.myweb.server.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import com.moogu.myweb.shared.utils.IlmsSharedException;

/**
 * Help to create {@link IlmsSharedException}
 * 
 * @author Jerome Angibaud
 */
public final class IlmsExceptionUtils {

    public static IlmsSharedException toSharedException(Throwable throwable, String code) {
        if (throwable instanceof IlmsSharedException) {
            return (IlmsSharedException) throwable;
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        IOUtils.closeQuietly(printWriter);
        String stringStackTrace = stringWriter.toString();
        IOUtils.closeQuietly(stringWriter);

        String causeClassName = throwable.getClass().getName();
        IlmsSharedException exception = new IlmsSharedException(
                        causeClassName,
                        throwable.getMessage(),
                        code,
                        stringStackTrace);
        return exception;
    }

    public static IlmsSharedException toSharedException(Throwable throwable) {
        return toSharedException(throwable, null);
    }

    public static IlmsSharedException toSharedException(String errorCode) {
        IlmsSharedException exception = new IlmsSharedException(null, null, errorCode, null);
        return exception;
    }

}
