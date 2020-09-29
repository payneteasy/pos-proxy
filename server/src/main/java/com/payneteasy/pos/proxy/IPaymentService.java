package com.payneteasy.pos.proxy;

import com.payneteasy.pos.proxy.messages.PaymentRequest;
import com.payneteasy.pos.proxy.messages.PaymentResponse;
import com.payneteasy.pos.proxy.messages.RefundRequest;

public interface IPaymentService {

    PaymentResponse pay(PaymentRequest aRequest);

    PaymentResponse refund(RefundRequest aRequest);

}
