package com.ImageProcessing.Image_Processing_Service.security.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtResponse {
    private String username;
    private String jwtToken;
}
