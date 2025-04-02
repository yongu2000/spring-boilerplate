package com.boilerplate.boilerplate.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UsernameDuplicateCheckResponse {

    private boolean isDuplicate;

    @JsonProperty("isDuplicate")
    public boolean isDuplicate() {
        return isDuplicate;
    }
}
