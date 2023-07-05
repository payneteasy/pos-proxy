package com.payneteasy.pos.proxy.messages;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class PaymentResponse {


    @NonNull
    private final String amount;

    @NonNull
    private final String currency;

    @NonNull
    private final String responseCode;

    private final String orderId;

}
