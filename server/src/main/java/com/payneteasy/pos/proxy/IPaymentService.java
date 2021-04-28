package com.payneteasy.pos.proxy;

import com.payneteasy.pos.proxy.messages.*;

public interface IPaymentService {

    PaymentResponse pay(PaymentRequest aRequest);

    PaymentResponse refund(RefundRequest aRequest);

    CloseDayResponse closeDay(CloseDayRequest aRequest);
}
