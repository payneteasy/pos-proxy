package com.payneteasy.pos.proxy.impl;

import com.payneteasy.inpas.sa.messages.sale.Sa1PaymentResponse;
import com.payneteasy.inpas.sa.messages.sale.Sa29ReversalResponse;
import com.payneteasy.pos.proxy.IPaymentService;
import com.payneteasy.pos.proxy.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class PaymentServiceImpl implements IPaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public PaymentResponse pay(PaymentRequest aRequest) {
        InpasNetworkManager manager = new InpasNetworkManager(aRequest.getAmount(), aRequest.getCurrency(), aRequest.getPosAddress());

        return manager.makeOperation(aClient -> {
            Sa1PaymentResponse saResponse = aClient.makeSale(
                    aRequest.getCurrency()
                    , new BigDecimal(aRequest.getAmount())
                    , aRequest.getTerminalId()
            );

            return PaymentResponse.builder()
                    .amount       ( saResponse.get_00_amount().toString()     )
                    .currency     ( aRequest.getCurrency()                    )
                    .orderId      ( saResponse.get_14_rrn()                   )
                    .responseCode ( saResponse.get_15_responseCode()          )
                    .build();
        });
    }

    @Override
    public PaymentResponse refund(RefundRequest aRequest) {
        InpasNetworkManager manager = new InpasNetworkManager(aRequest.getRefundAmount(), aRequest.getCurrency(), aRequest.getPosAddress());

        return manager.makeOperation(aClient -> {
            Sa29ReversalResponse saResponse = aClient.makeReversal(
                    aRequest.getCurrency()
                    , new BigDecimal(aRequest.getRefundAmount())
                    , aRequest.getOrderId()
                    , aRequest.getTerminalId()
            );

            return PaymentResponse.builder()
                    .amount       ( saResponse.get_00_amount().toString()     )
                    .currency     ( aRequest.getCurrency()                    )
                    .orderId      ( aRequest.getOrderId()                     )
                    .responseCode ( saResponse.get_15_responseCode()          )
                    .build();
        });
    }

    @Override
    public CloseDayResponse closeDay(CloseDayRequest aRequest) {
        InpasNetworkManager manager = new InpasNetworkManager("0", "RUB", aRequest.getPosAddress());

        PaymentResponse response = manager.makeOperation(aClient -> {
            aClient.makeReconciliation(aRequest.getTerminalId());
            return PaymentResponse.builder()
                    .responseCode("00")
                    .currency("RUB")
                    .amount("0")
                    .build();
        });

        return new CloseDayResponse(response.getResponseCode());
    }

    public static String toRrn(long aOrderId) {
        StringBuilder sb = new StringBuilder();
        sb.append(aOrderId);
        while(sb.length() < 11) {
            sb.insert(0, "0");
        }
        sb.insert(0, "P");
        return sb.toString();
    }

    private Long getPaynetOrderId(Sa1PaymentResponse saResponse) {
        if(saResponse.get_14_rrn() == null) {
            return null;
        }
        return Long.parseLong(saResponse.get_14_rrn().replace("P", ""));
    }
}
