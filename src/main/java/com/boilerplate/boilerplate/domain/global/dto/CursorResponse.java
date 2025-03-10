package com.boilerplate.boilerplate.domain.global.dto;

import java.util.List;

public record CursorResponse<T>(List<T> items, Long nextCursor, boolean hasNext) {

}
