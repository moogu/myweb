package com.moogu.myweb.server.feature.management;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.moogu.myweb.server.feature.management.RuntimeConfigurationService;
import com.moogu.myweb.test.common.AbstractIlmsIntegrationTest;

@TransactionConfiguration(defaultRollback = true)
public class RuntimeConfigurationServiceTest extends AbstractIlmsIntegrationTest {

    @Autowired
    private RuntimeConfigurationService runtimeConfigurationService;

    public void testSetGetProperties() {
        super.deleteFromTables("TB_PROPERTIES");

        // Test insert
        this.runtimeConfigurationService.setProperty("key1", "value1");
        final String value1 = this.runtimeConfigurationService.getProperty("key1");
        Assert.assertEquals(1, this.countRowsInTable("TB_PROPERTIES"));
        Assert.assertEquals("value1", value1);

        // Test update
        this.runtimeConfigurationService.setProperty("key1", "value2");
        Assert.assertEquals(1, this.countRowsInTable("TB_PROPERTIES"));
    }

}
