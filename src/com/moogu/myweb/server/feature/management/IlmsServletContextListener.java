package com.moogu.myweb.server.feature.management;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IlmsServletContextListener implements ServletContextListener {

    public final static String APP_CONTEXT_NAME = "ilmsApplicationContext";

    public void contextInitialized(ServletContextEvent contextEvent) {
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                        "applicationContext.xml",
                        "applicationContext-database.xml");
        contextEvent.getServletContext().setAttribute(APP_CONTEXT_NAME, applicationContext);
    }

    public void contextDestroyed(ServletContextEvent contextEvent) {
        final ClassPathXmlApplicationContext applicationContext = (ClassPathXmlApplicationContext) contextEvent.getServletContext()
                        .getAttribute(APP_CONTEXT_NAME);
        applicationContext.close();
    }

}
