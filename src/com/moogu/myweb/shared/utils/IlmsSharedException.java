package com.moogu.myweb.shared.utils;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This exception aims at pass back exception stack-trace from the server to the client. Generally you don't have to
 * instantiate it explicitly as the framework will do it for you : Runtime exception thrown at server boundary will be
 * be translated into this Exception so GWT will able to pass back exception stack trace to the client (in a string
 * form). However, if you want to pass a code info to the exception, you can explicitly instantiate this exception. It
 * is strongly advised to use IlmsExceptionUtils to do so.
 * 
 * At client side, call-back code should only deals with this exception.
 * 
 * @author Jerome Angibaud
 */
public class IlmsSharedException extends RuntimeException implements IsSerializable, Serializable {

    private static final long serialVersionUID = -7550900706157481267L;

    private String stringStackTrace;

    private String causeClassName; // ex 'java.lang.IllegalArgumentException'

    private String code;

    public IlmsSharedException(String causeClassName, String message, String code, String stringStackTrace) {
        super(message);
        this.stringStackTrace = stringStackTrace;
        this.causeClassName = causeClassName;
        this.code = code;
    }

    @SuppressWarnings("unused")
    // needed for serialization
    private IlmsSharedException() {
        super();
        this.stringStackTrace = null;
    }

    /**
     * As the stackTrace is not available at server side, this string use as a replacement and contains the stack-trace
     * in a string form.
     * 
     * @return
     */
    public String getStringStackTrace() {
        return this.stringStackTrace;
    }

    /**
     * As we may encounter to serialize exceptions, we just keep the class name of cause.
     */
    public String getCauseClassName() {
        return this.causeClassName;
    }

    /**
     * We can set an arbitrary code at exception instantiation.
     * 
     * @return
     */
    public String getCode() {
        return this.code;
    }

}
