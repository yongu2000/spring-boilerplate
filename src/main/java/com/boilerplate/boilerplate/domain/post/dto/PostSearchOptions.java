package com.boilerplate.boilerplate.domain.post.dto;

import com.boilerplate.boilerplate.domain.post.repository.constants.PostSearchType;
import com.boilerplate.boilerplate.domain.post.repository.constants.PostSortBy;
import com.boilerplate.boilerplate.domain.post.repository.constants.PostSortDirection;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchOptions {

    private PostSearchType searchType;        // 검색 유형 (title, content, author)
    private String searchKeyword;     // 검색어

    // 검색 기간
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;      // 시작일
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;        // 종료일

    // 정렬
    private PostSortBy sortBy = PostSortBy.DATE;   // 정렬 기준 (date, likes, comments)
    private PostSortDirection sortDirection = PostSortDirection.DESC; // 정렬 방향 (asc, desc)

    // 필터링
    @Min(0)
    private Long minViewCounts;        // 최소 조회수
    @Min(0)
    private Long minCommentCounts;     // 최소 댓글수
    @Min(0)
    private Long minLikes;        // 최소 좋아요수
}
