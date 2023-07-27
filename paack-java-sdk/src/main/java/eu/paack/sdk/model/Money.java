package eu.paack.sdk.model;

import lombok.Data;

@Data
public class Money {
    private String currency;
    private String amount;
}
