package tn.tradenet.elfatoora.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String username;
    private String displayName;
}
