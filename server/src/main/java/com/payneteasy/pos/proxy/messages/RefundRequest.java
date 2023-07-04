package com.payneteasy.pos.proxy.messages;

import lombok.Data;
import lombok.NonNull;

@Data
public class RefundRequest {

    @NonNull
    private final String refundAmount;

    /**
     * Paynet order id to refund
     */
    private final long orderId;

    @NonNull
    private final String currency;

    @NonNull
    private final String posAddress;

    @NonNull
    private final String posType;

    private final String terminalId;
}
