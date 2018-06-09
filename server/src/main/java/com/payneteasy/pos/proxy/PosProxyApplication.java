package com.payneteasy.pos.proxy;

import com.payneteasy.pos.proxy.impl.PaymentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PosProxyApplication {

    private static final Logger LOG = LoggerFactory.getLogger(PosProxyApplication.class);

    public static void main(String[] args) throws IOException {
        LOG.info("Initialising ...");
        LOG.info("Server {} starting on {}:{} ...", Configs.getVersion(), Configs.HTTP_SERVER_ADDRESS.asString(), Configs.HTTP_SERVER_PORT.asInt());
        IPaymentService service = new PaymentServiceImpl();
        WebServer       server  = new WebServer(service);
        server.start();
        server.registerShutdownHook();
    }
}
