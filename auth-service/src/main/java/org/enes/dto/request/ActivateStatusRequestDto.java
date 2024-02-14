package org.enes.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivateStatusRequestDto {
    private Long authId;
    private String activationCode;
}
