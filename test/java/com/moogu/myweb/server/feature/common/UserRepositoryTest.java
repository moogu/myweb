package com.moogu.myweb.server.feature.common;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.moogu.myweb.server.feature.common.UserRepository;
import com.moogu.myweb.shared.common.SUser;
import com.moogu.myweb.test.common.AbstractIlmsIntegrationTest;

@TransactionConfiguration(defaultRollback = true)
public class UserRepositoryTest extends AbstractIlmsIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    public void testCreateRenameAndGet() {
        this.deleteFromTables("tb_user");
        Assert.assertTrue(this.userRepository.getAllUsersInfo().isEmpty());
        final SUser user = this.userRepository.createUser("myCode", "myName");
        Assert.assertEquals(1, this.userRepository.getAllUsersInfo().size());
        Assert.assertEquals("myName", this.userRepository.findByCode("myCode").getName());
        this.userRepository.renameUser("myCode", "myNewName");
        Assert.assertEquals("myNewName", this.userRepository.findByCode("myCode").getName());

        final SUser user2 = this.userRepository.findById(user.getId());
        Assert.assertNotNull(user2);
        Assert.assertEquals("myCode", user2.getCode());
        Assert.assertEquals("myNewName", user2.getName());

        Assert.assertEquals(1, this.userRepository.getUsersOrderedByName().size());
    }

}
