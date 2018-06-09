package com.payneteasy.pos.proxy;

import com.payneteasy.pos.proxy.messages.PaymentRequest;
import com.payneteasy.pos.proxy.messages.PaymentResponse;

public interface IPaymentService {

    PaymentResponse pay(PaymentRequest aRequest);

}
