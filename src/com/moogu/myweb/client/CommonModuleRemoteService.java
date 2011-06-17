package com.moogu.myweb.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.moogu.myweb.client.common.ClientLog;
import com.moogu.myweb.shared.common.SUser;
import com.moogu.myweb.shared.utils.IlmsSharedException;

/**
 * Remote service to deal with currencies, currency groups, users,...
 * 
 * @author Jerome Angibaud
 */
@RemoteServiceRelativePath("commonService")
public interface CommonModuleRemoteService extends RemoteService {

    // This method is to declare explicitly IlmsSharedException as it will be 
    // in GWT serialization white list
    void dummy() throws IlmsSharedException;

    List<String> getProperetyKeys();

    Map<String, String> getProperties();

    List<SUser> getUsersOrderedByName();

    /**
     * Log information in the Log4J Server
     * 
     * @param log the type of Log. See ClientLog.Type
     * @param message the message to log
     */
    void log(ClientLog.Type log, String message);

    void renameUser(String code, String newName);

    void saveProperties(Map<String, String> props);
}
