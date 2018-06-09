package com.payneteasy.pos.proxy;

import com.payneteasy.pos.proxy.messages.PaymentRequest;
import com.payneteasy.pos.proxy.messages.PaymentResponse;
import one.nio.http.*;
import one.nio.server.AcceptorConfig;
import one.nio.util.ByteArrayBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class WebServer extends HttpServer {

    private static final Logger   LOG              = LoggerFactory.getLogger(WebServer.class);
    private static final Executor PAYMENT_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final String EXPECTED_API_KEY = ": " + Configs.API_KEY.asString();

    private final IPaymentService paymentService;

    public WebServer(IPaymentService aPaymentService) throws IOException {
        super(createConfig(Configs.HTTP_SERVER_ADDRESS.asString(), Configs.HTTP_SERVER_PORT.asInt()));
        paymentService = aPaymentService;
    }

    private static HttpServerConfig createConfig(String aAddress, int aPort) {
        AcceptorConfig acceptorConfig = new AcceptorConfig();
        acceptorConfig.address = aAddress;
        acceptorConfig.port = aPort;

        HttpServerConfig config = new HttpServerConfig();
        config.acceptors = new AcceptorConfig[]{acceptorConfig};
        config.selectors  = Configs.HTTP_SERVER_SELECTORS.asInt();
        config.minWorkers = Configs.HTTP_SERVER_MIN_WORKERS.asInt();
        config.maxWorkers = Configs.HTTP_SERVER_MAX_WORKERS.asInt();
        return config;

    }

    @Path("/pos-proxy/management/version.txt")
    public Response handleVersion() {
        return Response.ok(Configs.getVersion());
    }

    @Path("/pos-proxy/pay")
    public void pay(Request aRequest, HttpSession aSession) throws IOException {
        PAYMENT_EXECUTOR.execute(() -> {
            try {
                String apiKey = aRequest.getHeader("X-API-Key");

                if(!EXPECTED_API_KEY.equals(apiKey)) {
                    LOG.error("Expected X-API-Key is '{}' but was '{}'", EXPECTED_API_KEY, apiKey);
                    aSession.sendError(Response.FORBIDDEN, "Wrong X-API-Key");
                    return;
                }
                JsonMessages    json     = new JsonMessages(aRequest);
                PaymentRequest  request  = json.parse(PaymentRequest.class);
                PaymentResponse response = paymentService.pay(request);
                aSession.sendResponse(json.response(response));
            } catch (Exception e) {
                sendError("Error while processing pay", aSession, e);
            }
        });
    }

    @Override
    public void handleDefault(Request aRequest, HttpSession aSession) throws IOException {
        if(aRequest.getURI().startsWith("/pos-proxy/swagger-ui")) {
            swagger(aRequest, aSession);
        } else {
            super.handleDefault(aRequest, aSession);
        }
    }

    public void swagger(Request aRequest, HttpSession aSession) {
        asyncExecute(() -> {
            try {
                String filename = aRequest.getURI().substring("/pos-proxy/swagger-ui".length());
                if(filename.length() == 0) {
                    aSession.sendResponse(Response.redirect("/pos-proxy/swagger-ui/"));
                    return;
                }
                if(filename.length() == 1) {
                    filename = "/index.html";
                }
                String resource = "/swagger-ui" + filename;
                InputStream in = WebServer.class.getResourceAsStream(resource);
                if(in == null) {
                    LOG.warn("Resource {} -> {} not found", aRequest.getURI(), resource);
                    aSession.sendError(Response.NOT_FOUND, "Resource " + resource + " not found\n");
                    return;
                }
                ByteArrayBuilder byteArrayBuilder = readStream(in);
                aSession.sendResponse(new Response(Response.OK, byteArrayBuilder.toBytes()));
            } catch (Exception e) {
                sendError("Cannot process " + aRequest.getURI(), aSession, e);
            }
        });
    }

    public String getResourceName(String aUri) {
        return aUri;
    }

    private void sendError(String aMessage, HttpSession aSession, Exception e) {
        LOG.error("Error: {}", aMessage, e);
        try {
            aSession.sendError(Response.INTERNAL_ERROR, e.getMessage());
        } catch (IOException e1) {
            LOG.error("Cannot send error: {}", aMessage, e);
        }

    }

    private static ByteArrayBuilder readStream(InputStream in) throws IOException {
        byte[] buffer = new byte[64000];
        ByteArrayBuilder builder = new ByteArrayBuilder(buffer.length);
        for (int bytes; (bytes = in.read(buffer)) > 0; ) {
            builder.append(buffer, 0, bytes);
        }
        return builder;
    }


}
