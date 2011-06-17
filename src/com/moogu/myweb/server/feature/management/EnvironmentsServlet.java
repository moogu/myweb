package com.moogu.myweb.server.feature.management;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.moogu.myweb.server.utils.IlmsUtils;

public class EnvironmentsServlet extends HttpServlet {

    private static final long serialVersionUID = -1478115580037025670L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final PrintWriter out = resp.getWriter();

        out.println("Using getProperty = " + System.getProperty(IlmsUtils.SYSTEM_VARIABLE_ENVIRONMENT) + "<br/>");
        out.println("Using getEnv = " + System.getenv(IlmsUtils.SYSTEM_VARIABLE_ENVIRONMENT) + "<br/>");
        out.println("----------------------------<br/><br/><br/>Properties:");

        /** get system properties */
        final Properties pro = System.getProperties();
        for (final Entry<Object, Object> obj : pro.entrySet()) {
            out.println("<br/>[" + obj.getKey() + "] = [" + obj.getValue() + "]");
        }

        out.println("<br>----------------------------</br><br/><br/>Env:");

        /** get system properties */
        for (final Entry<String, String> entry : System.getenv().entrySet()) {
            out.println("<br/>[" + entry.getKey() + "] = [" + entry.getValue() + "]");
        }

        out.close();
    }
}