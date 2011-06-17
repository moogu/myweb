package com.moogu.myweb.server;

import com.moogu.myweb.client.CommonModuleRemoteService;

/**
 * RPC Servlet for Position Module. All method calls are redirected on the PositionModuleRemoteServiceImpl spring bean.
 * 
 * @author Jerome Angibaud
 */
public class CommonModuleRpcServlet extends AbstractGwtServlet {

    private static final long serialVersionUID = 57345442241138L;

    public CommonModuleRpcServlet() {
        super(false); // Transactions are managed by the spring bean
    }

    @Override
    protected Object getTargetObject() {
        return this.getApplicationContext().getBean(CommonModuleRemoteService.class);
    }

}
