package com.payneteasy.pos.proxy.impl;

import com.payneteasy.android.sdk.logger.ILogger;
import com.payneteasy.android.sdk.reader.inpas.config.InpasTerminalConfiguration;
import com.payneteasy.android.sdk.util.LoggerUtil;
import com.payneteasy.inpas.sa.client.SmartAgentClient;
import com.payneteasy.inpas.sa.messages.sale.*;

import java.io.IOException;
import java.math.BigDecimal;

public class InpasNetworkClientExtended {

    private static final ILogger LOG = LoggerUtil.create(InpasNetworkClientExtended.class);

    private final SmartAgentClient           delegate;
    private final String                     address;
    private final InpasTerminalConfiguration configuration;

    public InpasNetworkClientExtended(String aAddress, InpasTerminalConfiguration aConfiguration) {
        delegate      = new SmartAgentClient(aConfiguration.createNetworkClientOptions(aAddress));
        address       = aAddress;
        configuration = aConfiguration;
    }

    public void connect() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                delegate.openConnection();
                return;
            } catch (IOException e) {
                if(configuration.throwExceptionIfCannotConnect()) {
                    throw new IllegalStateException("Cannot connect to " + address, e);
                } else {
                    LOG.error("Cannot connect to {}. Sleeping 1 second ...", address, e);
                    Thread.sleep(1_000);
                }
            }
        }

    }

    public Sa1PaymentResponse makeSale(String aCurrency, BigDecimal aAmount, String aTerminalId) throws IOException {
        Sa1PaymentResponse saleResponse = delegate.sale(new Sa1PaymentRequest.Builder()
                ._04_currencyCode(643)
                ._00_amount(aAmount)
                ._26_stan(configuration.getInvoice())
                ._27_terminalId(aTerminalId)
                .build());
        return saleResponse;
    }

    public Sa29ReversalResponse makeReversal(String aCurrency, BigDecimal aAmount, String aRrn, String aTerminalId) throws IOException {

        Sa29ReversalRequest request  = new Sa29ReversalRequest.Builder()
                ._00_amount(aAmount)
                ._04_currencyCode(643)
                ._26_stan(configuration.getInvoice())
                ._14_rrn(aRrn)
                ._27_terminalId(aTerminalId)
                .build();
        return delegate.reversal(request);
    }

    public void makeReconciliation(String aTerminalId) {
        try {
            Sa59ReconciliationResponse reconciliation = delegate.reconciliation(new Sa59ReconciliationRequest.Builder()._27_terminalId(aTerminalId).build());
        } catch (IOException e) {
            LOG.error("Cannot make reconciliation", e);
        }
    }

    public void closeConnection() {
        try {
            delegate.close();
        } catch (Exception e) {
            LOG.error("Error while closing connection", e);
        }
    }

    public void sendEot() {
        try {
            delegate.sendEot();
        } catch (Exception e) {
            LOG.error("Cannot send EOT", e);
        }
    }

    public Sa26TestConnectionResponse checkConnection(String aTerminalId ) {
        try {
            LOG.debug("InpasNetworkClient; checkConnection new branch");
            return delegate.testConnectionToHost(aTerminalId);
        } catch (IOException e) {
            LOG.error("Error while testConnectionLocal", e);
        }

        return null;
    }
}
