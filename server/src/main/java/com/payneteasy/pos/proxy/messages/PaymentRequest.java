package com.payneteasy.pos.proxy.messages;

import lombok.Data;
import lombok.NonNull;

@Data
public class PaymentRequest {

    @NonNull
    private final String amount;

    @NonNull
    private final String currency;

    @NonNull
    private final String posAddress;

    @NonNull
    private final String posType;

    private final String terminalId;

}
