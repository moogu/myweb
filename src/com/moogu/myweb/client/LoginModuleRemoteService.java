package com.moogu.myweb.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.moogu.myweb.shared.utils.IlmsSharedException;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("loginService")
public interface LoginModuleRemoteService extends RemoteService {

    // This method is to declare explicitly IlmsSharedException as it will be 
    // in GWT serialization white list
    void dummy() throws IlmsSharedException;

    /**
     * Gives profile information about the user
     * 
     * @return
     */
    Map<String, String> getUserInfo();
}