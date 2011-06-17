package com.moogu.myweb.server;

import com.moogu.myweb.client.ReserveModuleRemoteService;

/**
 * RPC Servlet for EUR Reserve. All method calls are redirected on the ReserveModuleRemoteServiceImpl spring bean.
 * 
 * @author i21726 - Patrick Santana
 */
public class ReserveModuleRpcServlet extends AbstractGwtServlet {

    private static final long serialVersionUID = 7443678182098279121L;

    public ReserveModuleRpcServlet() {
        super(false);
    }

    @Override
    protected Object getTargetObject() {
        return this.getApplicationContext().getBean(ReserveModuleRemoteService.class);
    }

}
