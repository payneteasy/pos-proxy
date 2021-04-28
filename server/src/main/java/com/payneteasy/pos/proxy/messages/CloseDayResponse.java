package com.payneteasy.pos.proxy.messages;

import lombok.Data;
import lombok.NonNull;

@Data
public class CloseDayResponse {

    @NonNull
    private final String responseCode;

}
