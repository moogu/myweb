package com.moogu.myweb.server;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.moogu.myweb.client.CommonModuleRemoteService;
import com.moogu.myweb.client.common.ClientLog;
import com.moogu.myweb.server.feature.common.UserRepository;
import com.moogu.myweb.server.feature.management.ClientLogger;
import com.moogu.myweb.server.feature.management.RuntimeConfigurationService;
import com.moogu.myweb.shared.common.SUser;
import com.moogu.myweb.shared.utils.IlmsSharedException;

@Component
@Transactional(readOnly = false, timeout = 15)
public class CommonModuleRemoteServiceImpl implements CommonModuleRemoteService {

    @Autowired
    private RuntimeConfigurationService configurationService;

    @Autowired
    private UserRepository userRepository;

    public void dummy() throws IlmsSharedException {
    }

    public List<String> getProperetyKeys() {
        return Arrays.asList(RuntimeConfigurationService.KEYS);
    }

    public Map<String, String> getProperties() {
        return configurationService.getPropertes();
    }

    public List<SUser> getUsersOrderedByName() {
        return userRepository.getUsersOrderedByName();
    }

    public void log(ClientLog.Type log, String message) {
        ClientLogger.log(log, message);
    }

    public void renameUser(String code, String newName) {
        userRepository.renameUser(code, newName);
    }

    public void saveProperties(Map<String, String> props) {
        configurationService.deleteAllProperties();
        for (final Map.Entry<String, String> prop : props.entrySet()) {
            final String key = prop.getKey();
            if (!StringUtils.isBlank(key)) {
                configurationService.setProperty(key, prop.getValue());
            }
        }

    }
}
