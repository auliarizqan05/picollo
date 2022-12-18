package com.app.picollo.domain.transaction.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class TransferRequest {
    
    BigDecimal amount;
    @JsonProperty("to_username")
    String toUsername;
}
