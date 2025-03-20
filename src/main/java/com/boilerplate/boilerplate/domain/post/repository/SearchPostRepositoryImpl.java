package com.boilerplate.boilerplate.domain.post.repository;

import static com.boilerplate.boilerplate.domain.post.entity.QPost.post;
import static com.boilerplate.boilerplate.domain.post.repository.constants.PostSortDirection.ASC;
import static com.boilerplate.boilerplate.domain.user.entity.QUser.user;
import static org.springframework.util.StringUtils.hasText;

import com.boilerplate.boilerplate.domain.post.dto.PostSearchOptions;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.repository.constants.PostSearchType;
import com.boilerplate.boilerplate.domain.post.repository.constants.PostSortBy;
import com.boilerplate.boilerplate.domain.post.repository.constants.PostSortDirection;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class SearchPostRepositoryImpl implements SearchPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> findBySearchOptions(Pageable pageable, PostSearchOptions searchOptions) {
        List<Post> content = queryFactory
            .selectFrom(post)
            .leftJoin(post.user, user).fetchJoin()
            .where(
                searchKeywordContains(searchOptions.getSearchType(), searchOptions.getSearchKeyword()),
                createdDateBetween(searchOptions.getStartDate(), searchOptions.getEndDate()),
                viewCountsGoe(searchOptions.getMinViewCounts()),
                commentCountsGoe(searchOptions.getMinCommentCounts()),
                likesGoe(searchOptions.getMinLikes())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifier(searchOptions.getSortBy(), searchOptions.getSortDirection()))
            .fetch();

        Long count = queryFactory
            .select(post.count())
            .from(post)
            .leftJoin(post.user, user)
            .where(
                searchKeywordContains(searchOptions.getSearchType(), searchOptions.getSearchKeyword()),
                createdDateBetween(searchOptions.getStartDate(), searchOptions.getEndDate()),
                viewCountsGoe(searchOptions.getMinViewCounts()),
                commentCountsGoe(searchOptions.getMinCommentCounts()),
                likesGoe(searchOptions.getMinLikes())
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, count != null ? count : 0L);
    }

    private OrderSpecifier<?> getOrderSpecifier(PostSortBy sortBy, PostSortDirection sortDirection) {
        return switch (sortBy) {
            case VIEWS -> sortDirection == ASC ? post.viewCounts.asc() : post.viewCounts.desc();
            case COMMENTS -> sortDirection == ASC ? post.commentCounts.asc() : post.commentCounts.desc();
            case LIKES -> sortDirection == ASC ? post.likes.asc() : post.likes.desc();
            default -> sortDirection == ASC ? post.createdAt.asc() : post.createdAt.desc();
        };
    }

    private BooleanExpression likesGoe(Long minLikes) {
        return minLikes != null ? post.likes.goe(minLikes) : null;
    }

    private BooleanExpression commentCountsGoe(Long minCommentCounts) {
        return minCommentCounts != null ? post.commentCounts.goe(minCommentCounts) : null;
    }

    private BooleanExpression viewCountsGoe(Long minViews) {
        return minViews != null ? post.viewCounts.goe(minViews) : null;
    }

    private BooleanExpression createdDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        if (startDate != null && endDate != null) {
            return post.createdAt.between(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
            );
        }

        if (startDate != null) {
            return post.createdAt.goe(startDate.atStartOfDay());
        }

        return post.createdAt.lt(endDate.plusDays(1).atStartOfDay());
    }
    
    private BooleanExpression searchKeywordContains(PostSearchType searchType, String searchKeyword) {
        if (!hasText(searchKeyword)) {
            return null;
        }

        return switch (searchType) {
            case TITLE -> post.title.containsIgnoreCase(searchKeyword);
            case CONTENT -> post.content.containsIgnoreCase(searchKeyword);
            case AUTHOR -> post.user.name.containsIgnoreCase(searchKeyword);
        };
    }
}
