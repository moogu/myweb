package com.moogu.myweb.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.moogu.myweb.client.MyWebEntryPoint;
import com.moogu.myweb.client.LoginModuleRemoteService;
import com.moogu.myweb.server.feature.common.UserRepository;
import com.moogu.myweb.shared.common.SUser;
import com.moogu.myweb.shared.utils.IlmsSharedException;

/**
 * The server side implementation of the RPC service.<br>
 * This class also starts Spring Framework when loaded for the first time.
 */
@SuppressWarnings("serial")
public class LoginModuleRemoteServiceImpl extends AbstractGwtServlet implements LoginModuleRemoteService {

    /** for log */
    private final static Logger logger = Logger.getLogger(LoginModuleRemoteServiceImpl.class.getName());

    public void dummy() throws IlmsSharedException {
    }

    @Override
    protected Object getTargetObject() {
        return this;
    }

    /**
     * 
     * @see com.moogu.myweb.client.LoginModuleRemoteService#getUserInfo()
     */
    public Map<String, String> getUserInfo() {

        final Map<String, String> result = new HashMap<String, String>();

        /** user principal name */
        String strUserConnected = getUser();

        /** verify if it is on database */
        SUser objUser = getUserRepository().findByCode(strUserConnected);
        if (objUser == null) {
            if (strUserConnected == null) {
                strUserConnected = "DEV";
            }
            objUser = getUserRepository().createUser(strUserConnected, "");
        }

        /** user name */
        result.put("USER_CODE", objUser.getCode());
        /** the id of the user */
        result.put("USER_ID", objUser.getId().toString());

        /** the roles */
        result.put(MyWebEntryPoint.ROLE_1, String.valueOf(getThreadLocalRequest().isUserInRole(MyWebEntryPoint.ROLE_1)));
        result.put(MyWebEntryPoint.ROLE_2, String.valueOf(getThreadLocalRequest().isUserInRole(MyWebEntryPoint.ROLE_2)));
        result.put(MyWebEntryPoint.ROLE_3, String.valueOf(getThreadLocalRequest().isUserInRole(MyWebEntryPoint.ROLE_3)));
        result.put(MyWebEntryPoint.ROLE_4, String.valueOf(getThreadLocalRequest().isUserInRole(MyWebEntryPoint.ROLE_4)));

        /** if DEV, set all permissions */
        if (objUser.getCode().equals("DEV")) {
            result.put(MyWebEntryPoint.ROLE_1, String.valueOf(true));
            result.put(MyWebEntryPoint.ROLE_2, String.valueOf(true));
            result.put(MyWebEntryPoint.ROLE_3, String.valueOf(true));
            result.put(MyWebEntryPoint.ROLE_4, String.valueOf(true));
        }

        /** log information with debug */
        if (LoginModuleRemoteServiceImpl.logger.isDebugEnabled()) {
            LoginModuleRemoteServiceImpl.logger.debug("User id = " + objUser.getId());
            LoginModuleRemoteServiceImpl.logger.debug("User Principal = " + objUser.getCode());
            LoginModuleRemoteServiceImpl.logger.debug("is User " + MyWebEntryPoint.ROLE_1 + " = " + result.get(MyWebEntryPoint.ROLE_1));
            LoginModuleRemoteServiceImpl.logger.debug("is User " + MyWebEntryPoint.ROLE_2 + " = " + result.get(MyWebEntryPoint.ROLE_2));
            LoginModuleRemoteServiceImpl.logger.debug("is User " + MyWebEntryPoint.ROLE_3 + " = " + result.get(MyWebEntryPoint.ROLE_3));
            LoginModuleRemoteServiceImpl.logger.debug("is User " + MyWebEntryPoint.ROLE_4 + " = " + result.get(MyWebEntryPoint.ROLE_4));
        }

        return result;
    }

    private UserRepository getUserRepository() {
        return getApplicationContext().getBean(UserRepository.class);
    }

}