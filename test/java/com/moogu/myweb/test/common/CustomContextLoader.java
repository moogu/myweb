package com.moogu.myweb.test.common;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextLoader;

/**
 * Class used to create Spring application context within tests
 * 
 * @author i19451
 */
class CustomContextLoader implements ContextLoader {

    public ApplicationContext loadContext(String... locations) throws Exception {
        return TestHelper.createPersoDbApplicationContext();
    }

    public String[] processLocations(Class<?> clazz, String... locations) {
        return locations;
    }

}