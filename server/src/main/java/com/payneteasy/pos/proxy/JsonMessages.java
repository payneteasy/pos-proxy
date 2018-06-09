package com.payneteasy.pos.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import one.nio.http.HttpException;
import one.nio.http.Request;
import one.nio.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class JsonMessages {

    private static final Logger LOG = LoggerFactory.getLogger(JsonMessages.class);

    private static final Charset UTF_8 = Charset.forName("utf-8");
    private static final Gson    GSON  = new GsonBuilder().setPrettyPrinting().create();

    private final Request request;
    private final String  url;

    public JsonMessages(Request aRequest) {
        request = aRequest;
        url = aRequest.getURI();
    }

    public <T> T parse(Class<T> aClass) throws HttpException {
        if(request.getBody() == null) {
            throw new HttpException("No body");
        }
        String json = new String(request.getBody(), UTF_8);
        LOG.debug("IN {} = {}", url, json);
        return GSON.fromJson(json, aClass);
    }

    public Response response(Object aObject) {
        String json = GSON.toJson(aObject);
        LOG.debug("OUT {} = {}", url, json);
        Response response = new Response(Response.OK, json.getBytes(UTF_8));
        response.addHeader("Content-Type: application/json");
        return response;
    }

}
