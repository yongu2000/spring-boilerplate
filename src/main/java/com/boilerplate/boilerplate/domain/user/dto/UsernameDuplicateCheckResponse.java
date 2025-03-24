package com.boilerplate.boilerplate.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsernameDuplicateCheckResponse {

    @JsonProperty("isDuplicate")
    private boolean isDuplicate;
}
