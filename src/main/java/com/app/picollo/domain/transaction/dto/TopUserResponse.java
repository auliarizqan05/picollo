package com.app.picollo.domain.transaction.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TopUserResponse {

    String username;
    @JsonProperty("transacted_value")
    BigDecimal transactedValue;
    
}
