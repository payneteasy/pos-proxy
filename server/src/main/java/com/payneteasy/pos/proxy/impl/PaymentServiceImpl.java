package com.payneteasy.pos.proxy.impl;

import com.payneteasy.android.sdk.reader.inpas.config.InpasTerminalConfiguration;
import com.payneteasy.android.sdk.reader.inpas.config.InpasTerminalPacketOptions;
import com.payneteasy.android.sdk.reader.inpas.network.InpasNetworkClient;
import com.payneteasy.inpas.sa.messages.sale.Sa1PaymentResponse;
import com.payneteasy.inpas.sa.network.handlers.DefaultClientPacketOptions;
import com.payneteasy.pos.proxy.IPaymentService;
import com.payneteasy.pos.proxy.messages.PaymentRequest;
import com.payneteasy.pos.proxy.messages.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;

public class PaymentServiceImpl implements IPaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public PaymentResponse pay(PaymentRequest aRequest) {
        InpasNetworkClient client = new InpasNetworkClient(aRequest.posAddress, new InpasTerminalConfiguration.Builder()
                .packetOptions(new DefaultClientPacketOptions())
                .throwExceptionIfCannotConnect(true)
                .build()
        );
        try {
            client.connect();
            try {
                Sa1PaymentResponse saResponse = client.makeSale(aRequest.currency, new BigDecimal(aRequest.amount));

                PaymentResponse response = new PaymentResponse();
                response.amount       = saResponse.get_00_amount().toString();
                response.currency     = "RUB";
                response.orderId      = getPaynetOrderId(saResponse);
                response.responseCode = saResponse.get_15_responseCode();
                return response;

            } catch (Exception e) {
                client.sendEot();
                LOG.error("Cannot process a payment", e);
                return error(aRequest, "-1");
            } finally {
                client.closeConnection();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted");
        } catch (Exception e) {
            LOG.error("Cannot connect", e);
            return error(aRequest, "-2");
        }
    }

    private PaymentResponse error(PaymentRequest aRequest, String aErrorCode) {
        PaymentResponse response = new PaymentResponse();
        response.amount       = aRequest.amount;
        response.currency     = "RUB";
        response.orderId      = null;
        response.responseCode = aErrorCode;
        return response;
    }

    private Long getPaynetOrderId(Sa1PaymentResponse saResponse) {
        if(saResponse.get_14_rrn() == null) {
            return null;
        }
        return Long.parseLong(saResponse.get_14_rrn().replace("P", ""));
    }
}
