package com.moogu.myweb.server.utils;

import javax.servlet.http.HttpServletRequest;

public class IlmsHttpRequestThreadLocal {

    private static ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();

    public static void clean() {
        IlmsHttpRequestThreadLocal.request.remove();
    }

    public static HttpServletRequest get() {
        return IlmsHttpRequestThreadLocal.request.get();
    }

    public static void set(HttpServletRequest request) {
        IlmsHttpRequestThreadLocal.request.set(request);
    }
}
