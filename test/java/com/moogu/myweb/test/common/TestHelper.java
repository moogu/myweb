package com.moogu.myweb.test.common;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.moogu.myweb.server.utils.IlmsUtils;
import com.moogu.myweb.shared.utils.IlmsEnvironmentEnum;

/**
 * Hold some convenient methods for testing purpose.
 * 
 * @author i19451
 */
public final class TestHelper {

    /**
     * Do not forget to close the application context after use ( {@link AbstractApplicationContext#close()} If not it
     * will lead in database connection leak.
     */
    public static AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("applicationContext.xml", "applicationContext-database.xml");
    }

    public static AbstractApplicationContext createPersoDbApplicationContext() {
        return new ClassPathXmlApplicationContext("applicationContext.xml", "applicationContext-personal-database.xml");
    }

    public static TransactionStatus getTransaction(ApplicationContext applicationContext) {
        final PlatformTransactionManager platformTransactionManager = applicationContext.getBean(PlatformTransactionManager.class);
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        def.setTimeout(3 * 60); // 3 minutes timeout
        final TransactionStatus transactionStatus = platformTransactionManager.getTransaction(def);
        return transactionStatus;
    }

    public static void rollback(ApplicationContext applicationContext, TransactionStatus transactionStatus) {
        final PlatformTransactionManager platformTransactionManager = applicationContext.getBean(PlatformTransactionManager.class);
        platformTransactionManager.rollback(transactionStatus);
    }

    public static void commit(ApplicationContext applicationContext, TransactionStatus transactionStatus) {
        final PlatformTransactionManager platformTransactionManager = applicationContext.getBean(PlatformTransactionManager.class);
        platformTransactionManager.commit(transactionStatus);
    }

    /**
     * Populate autowired fields. Usefull to define runner class in test
     * 
     * @param instanceToPopulate
     * @param applicationContext
     */
    public static void fillAutowiredFields(Object instanceToPopulate, ApplicationContext applicationContext) {
        final Class<?> clazz = instanceToPopulate.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            final Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
            if (autowiredAnnotation != null) {
                final Class<?> fieldClass = field.getType();
                final Object springBean = applicationContext.getBean(fieldClass);
                try {
                    field.setAccessible(true);
                    field.set(instanceToPopulate, springBean);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static <T> T createPopulatedInstance(Class<T> beanType, ApplicationContext applicationContext) {
        try {
            final T instance = beanType.newInstance();
            TestHelper.fillAutowiredFields(instance, applicationContext);
            return instance;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T createPopulatedInstance(Class<T> beanType) {
        final ApplicationContext applicationContext = TestHelper.createPersoDbApplicationContext();
        return TestHelper.createPopulatedInstance(beanType, applicationContext);
    }

    public static void switchEnvironment(IlmsEnvironmentEnum environment) {
        System.setProperty(IlmsUtils.SYSTEM_VARIABLE_ENVIRONMENT, environment.name());
    }

}
