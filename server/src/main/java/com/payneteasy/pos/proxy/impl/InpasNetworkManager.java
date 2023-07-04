package com.payneteasy.pos.proxy.impl;

import com.payneteasy.android.sdk.reader.inpas.config.InpasTerminalConfiguration;
import com.payneteasy.android.sdk.reader.inpas.network.InpasNetworkClient;
import com.payneteasy.inpas.sa.client.handlers.DefaultClientPacketOptions;
import com.payneteasy.pos.proxy.messages.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InpasNetworkManager {

    private static final Logger LOG = LoggerFactory.getLogger(InpasNetworkManager.class);

    private final String amount;
    private final String currency;
    private final String posAddress;

    public InpasNetworkManager(String amount, String currency, String aPosAddress) {
        this.amount = amount;
        this.currency = currency;
        posAddress = aPosAddress;
    }

    public interface IInpasOperationHandler {
        PaymentResponse invokeOperation(InpasNetworkClientExtended aClient) throws IOException;
    }

    public PaymentResponse makeOperation(IInpasOperationHandler aHandler) {
        InpasNetworkClientExtended client = new InpasNetworkClientExtended(posAddress, new InpasTerminalConfiguration.Builder()
                .packetOptions(new DefaultClientPacketOptions())
                .throwExceptionIfCannotConnect(true)
                .build()
        );
        try {
            client.connect();
            try {

                return aHandler.invokeOperation(client);

            } catch (Exception e) {
                client.sendEot();
                LOG.error("Cannot process a payment", e);
                return error("-1");
            } finally {
                client.closeConnection();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted");
        } catch (Exception e) {
            LOG.error("Cannot connect", e);
            return error("-2");
        }

    }

    private PaymentResponse error(String aErrorCode) {
        return PaymentResponse.builder()
                .amount       ( amount   )
                .currency     ( currency )
                .orderId      ( null                   )
                .responseCode ( aErrorCode             )
                .build();
    }


}
