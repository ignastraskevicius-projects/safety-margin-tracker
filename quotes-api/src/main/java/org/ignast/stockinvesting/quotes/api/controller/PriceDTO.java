package org.ignast.stockinvesting.quotes.api.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PriceDTO {

    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final BigDecimal amount;

    @NonNull
    private final String currency;
}
