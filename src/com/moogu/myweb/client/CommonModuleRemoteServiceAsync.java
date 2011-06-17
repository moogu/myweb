package com.moogu.myweb.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.moogu.myweb.client.common.ClientLog;
import com.moogu.myweb.shared.common.SUser;

public interface CommonModuleRemoteServiceAsync {

    void dummy(AsyncCallback<Void> callback);

    void getProperetyKeys(AsyncCallback<List<String>> callback);

    void getProperties(AsyncCallback<Map<String, String>> callback);

    void getUsersOrderedByName(AsyncCallback<List<SUser>> ilmsAsyncCallback);

    void log(ClientLog.Type log, String message, AsyncCallback<Void> callback);

    void renameUser(String code, String newName, AsyncCallback<Void> ilmsAsyncCallback);

    void saveProperties(Map<String, String> props, AsyncCallback<Void> callback);
}
