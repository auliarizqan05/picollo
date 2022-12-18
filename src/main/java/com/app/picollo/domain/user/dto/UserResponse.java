package com.app.picollo.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    String token;
    
}
