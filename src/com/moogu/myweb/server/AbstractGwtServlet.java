package com.moogu.myweb.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamWriter;
import com.moogu.myweb.server.feature.management.IlmsServletContextListener;
import com.moogu.myweb.server.utils.IlmsExceptionUtils;
import com.moogu.myweb.server.utils.IlmsHttpRequestThreadLocal;
import com.moogu.myweb.server.utils.IlmsUserThreadLocal;

/**
 * The goal of this Servlet is to provide a base GWT Servlet that :
 * <ul>
 * <li>handle transaction opening/closing - handle</li>
 * <li>handle gracefully exception, as we want to get the exception stack-trace back to client without having to declare
 * exception in remote interface</li>
 * <li>provide basic technical method as getting applicationContext, user, ..</li>
 * <li>Bind the user to the current thread</local>
 * </ul>
 * 
 * @author i19451
 */
public abstract class AbstractGwtServlet extends RemoteServiceServlet {

    private static final long serialVersionUID = 1L;

    private final static Logger logger = Logger.getLogger(AbstractGwtServlet.class);

    private final boolean manageTransaction;

    private ApplicationContext applicationContext;

    /**
     * Object on which the method will be invoked upon
     */
    private Object targetObject;

    public AbstractGwtServlet(boolean manageTransaction) {
        this.manageTransaction = manageTransaction;
    }

    public AbstractGwtServlet() {
        this(true);
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        this.applicationContext = (ApplicationContext) servletConfig.getServletContext().getAttribute(
                        IlmsServletContextListener.APP_CONTEXT_NAME);
        this.targetObject = this.getTargetObject();
    }

    public void dummyMethodException() {
    }

    protected abstract Object getTargetObject();

    protected ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    protected <T> T getSpringBean(Class<T> beanClass) {
        return this.getApplicationContext().getBean(beanClass);
    }

    @Override
    public String processCall(String payload) throws SerializationException {

        // Prepare transaction if needed
        TransactionStatus transactionStatus = null;
        PlatformTransactionManager transactionManager = null;
        if (this.manageTransaction) {
            transactionManager = this.getApplicationContext().getBean(PlatformTransactionManager.class);
            transactionStatus = transactionManager.getTransaction(this.createTxDef());
        }

        // perform code invocation.
        IlmsUserThreadLocal.setUser(this.getUser());
        InvokeResponse invokeResponse;

        // Commit or roll-back transaction
        try {
            IlmsHttpRequestThreadLocal.set(this.getThreadLocalRequest());

            invokeResponse = this.invokeAndEncodeResponse(payload);
            if (this.manageTransaction) {
                if (invokeResponse.isSuccess()) {
                    transactionManager.commit(transactionStatus);
                } else {
                    AbstractGwtServlet.logger.error("Error intercepted at server boundary : ", invokeResponse.t);
                    try {
                        transactionManager.rollback(transactionStatus);
                    } catch (final RuntimeException txe) {
                        AbstractGwtServlet.logger.error("Tx failed to rollback", txe);
                    }
                }
            }
        } finally {
            IlmsUserThreadLocal.cleanUser();
            IlmsHttpRequestThreadLocal.clean();
        }

        return invokeResponse.responsePayload;

    }

    protected InvokeResponse invokeAndEncodeResponse(String payload) {

        try {
            final RPCRequest rpcRequest = RPC.decodeRequest(payload, this.targetObject.getClass(), this);
            this.onAfterRequestDeserialized(rpcRequest);
            return this.invokeAndEncodeResponse(
                            this.targetObject,
                            rpcRequest.getMethod(),
                            rpcRequest.getParameters(),
                            rpcRequest.getSerializationPolicy(),
                            rpcRequest.getFlags());
        } catch (final Exception e) {
            AbstractGwtServlet.logger.error("Payload from error: " + payload);
            AbstractGwtServlet.logger.error(e.getMessage(), e);
            return new InvokeResponse(e.getMessage(), e);
        }

    }

    protected InvokeResponse invokeAndEncodeResponse(Object target,
                    Method serviceMethod,
                    Object args[],
                    SerializationPolicy serializationPolicy,
                    int flags) throws SerializationException {

        String responsePayload;

        try {
            final Object result = serviceMethod.invoke(target, args);
            responsePayload = RPC.encodeResponseForSuccess(serviceMethod, result, serializationPolicy, flags);
            return new InvokeResponse(responsePayload);
        } catch (final Exception e) {
            final Throwable cause = e.getCause();

            final Throwable returnedException;
            if (cause instanceof RuntimeException) {
                AbstractGwtServlet.logger.error("Error while calling server method", cause);
                returnedException = IlmsExceptionUtils.toSharedException(cause);
            } else if (e instanceof InvocationTargetException) {
                final InvocationTargetException ite = (InvocationTargetException) e;
                AbstractGwtServlet.logger.error("Error while calling server method", ite);
                returnedException = IlmsExceptionUtils.toSharedException(ite.getTargetException());
            } else if (cause != null) {
                AbstractGwtServlet.logger.error("Error while calling server method", cause);
                returnedException = IlmsExceptionUtils.toSharedException(cause);
            } else {
                AbstractGwtServlet.logger.error("Error while calling server method", e);
                returnedException = IlmsExceptionUtils.toSharedException(e);
            }

            responsePayload = AbstractGwtServlet.encodeResponseForFailure(
                            serviceMethod,
                            returnedException,
                            serializationPolicy,
                            flags);

            return new InvokeResponse(responsePayload, cause);
        }

    }

    private static String encodeResponseForFailure(Method serviceMethod,
                    Throwable cause,
                    SerializationPolicy serializationPolicy,
                    int flags) throws SerializationException {
        if (cause == null) {
            throw new NullPointerException("cause cannot be null");
        }
        if (serializationPolicy == null) {
            throw new NullPointerException("serializationPolicy");
        }

        return AbstractGwtServlet.encodeResponse(cause.getClass(), cause, true, flags, serializationPolicy);

    }

    private static String encodeResponse(Class<?> responseClass,
                    Object object,
                    boolean wasThrown,
                    int flags,
                    SerializationPolicy serializationPolicy) throws SerializationException {
        final ServerSerializationStreamWriter stream = new ServerSerializationStreamWriter(serializationPolicy);
        stream.setFlags(flags);
        stream.prepareToWrite();
        if (responseClass != Void.TYPE) {
            stream.serializeValue(object, responseClass);
        }
        final String bufferStr = (new StringBuilder()).append(wasThrown ? "//EX" : "//OK")
                        .append(stream.toString())
                        .toString();
        return bufferStr;
    }

    protected DefaultTransactionDefinition createTxDef() {
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        def.setTimeout(240); // 240 seconds timeout: processes as account synchro requires a lot of time
        return def;
    }

    protected String getUser() {
        String strUserConnected = "DEV";
        final Principal principal = this.getThreadLocalRequest().getUserPrincipal();
        if (principal != null) {
            strUserConnected = principal.getName();
        }
        return strUserConnected;
    }

    public static class InvokeResponse {

        public final String responsePayload;

        public final Throwable t;

        public InvokeResponse(String responsePayload) {
            super();
            this.responsePayload = responsePayload;
            this.t = null;
        }

        public InvokeResponse(String responsePayload, Throwable t) {
            super();
            this.responsePayload = responsePayload;
            this.t = t;
        }

        public boolean isSuccess() {
            return this.t == null;
        }

    }

}
