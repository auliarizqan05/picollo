package com.app.picollo.domain.transaction.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopTransactionPerUserResponse {

    String username;
    BigDecimal amount;
    
}
