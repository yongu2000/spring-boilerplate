package com.boilerplate.boilerplate.dev;

import com.boilerplate.boilerplate.domain.user.entity.Role;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyDataBatchInsert implements CommandLineRunner {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss.SSS");

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {

//        deleteAllData();

        log.info("[{}] Jdbc Data Insertion Started", LocalDateTime.now().format(formatter));

        long start = System.currentTimeMillis();

        insertDummyData();

        long end = System.currentTimeMillis();
        log.info("[{}] Jdbc Data Insertion Finished in {}ms", LocalDateTime.now().format(formatter),
            end - start);
    }

    private void deleteAllData() {
        jdbcTemplate.update("DELETE FROM comment");
        jdbcTemplate.update("DELETE FROM post");
        jdbcTemplate.update("DELETE FROM user");
        log.info("모든 데이터 삭제 완료");
    }

    public void insertDummyData() {
        int userCount = 50;
        int postCount = 10000;
        int commentsPerPost = 5;
        int repliesPerComment = 2;

        log.info("👉 유저 생성 시작");
        insertUsers(userCount);
        log.info("✅ 유저 생성 완료");

        log.info("👉 게시글 생성 시작");
        insertPosts(postCount, userCount);
        log.info("✅ 게시글 생성 완료");

        log.info("👉 댓글 생성 시작");
        insertComments(postCount, userCount, commentsPerPost);
        log.info("✅ 댓글 생성 완료");

        log.info("👉 대댓글 생성 시작");
        insertReplies(postCount, commentsPerPost, repliesPerComment, userCount);
        log.info("✅ 대댓글 생성 완료");
    }

    private void insertUsers(int count) {

        String sql = "INSERT INTO user (email, username, password, name, profile_image_url, role, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchArgs = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            batchArgs.add(new Object[]{
                "user" + i + "@example.com",
                "user" + i,
                "password" + i,
                "User " + i,
                "/uploads/image/default.jpg",
                Role.USER.name(),
                LocalDateTime.now()
            });
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void insertPosts(int count, int userCount) {
        String sql = "INSERT INTO post (title, content, likes, comment_counts, view_counts, user_id, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, "게시글 제목 " + (i + 1));
                ps.setString(2, "이것은 " + (i + 1) + "번째 게시글의 내용입니다.");
                ps.setLong(3, 0L);
                ps.setLong(4, 5L); // 댓글 수 고정
                ps.setLong(5, 0L);
                ps.setLong(6, (i % userCount) + 1); // user_id 1 ~ userCount
                ps.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis() - i * 1000L));
                ps.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis() - i * 1000L));

            }

            @Override
            public int getBatchSize() {
                return count;
            }
        });
    }

    private void insertComments(int postCount, int userCount, int commentsPerPost) {
        String sql = "INSERT INTO comment (content, post_id, user_id, parent_comment_id, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int index) throws SQLException {
                int postId = (index / commentsPerPost) + 1;
                int commentIndex = index % commentsPerPost + 1;

                ps.setString(1, "Post " + postId + "에 대한 " + commentIndex + "번째 댓글입니다.");
                ps.setInt(2, postId);
                ps.setInt(3, (index % userCount) + 1);
                ps.setObject(4, null);
                ps.setTimestamp(5,
                    new java.sql.Timestamp(System.currentTimeMillis() - index * 1000L));
                ps.setTimestamp(6,
                    new java.sql.Timestamp(System.currentTimeMillis() - index * 1000L));
                ; // parent_comment_id 없음 = 최상위 댓글
            }

            @Override
            public int getBatchSize() {
                return postCount * commentsPerPost;
            }
        });
    }

    private void insertReplies(int postCount, int commentsPerPost, int repliesPerComment,
        int userCount) {
        String sql = "INSERT INTO comment (content, post_id, user_id, parent_comment_id, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int index) throws SQLException {
                int commentIndex = index / repliesPerComment + 1; // 부모 댓글 id (1부터 시작)
                int replyNumber = (index % repliesPerComment) + 1;

                int postId = ((commentIndex - 1) / commentsPerPost) + 1;

                ps.setString(1, "댓글 " + commentIndex + "에 대한 대댓글 " + replyNumber + "입니다.");
                ps.setInt(2, postId);
                ps.setInt(3, (index % userCount) + 1);
                ps.setLong(4, commentIndex);
                ps.setTimestamp(5,
                    new java.sql.Timestamp(System.currentTimeMillis() - index * 1000L));
                ps.setTimestamp(6,
                    new java.sql.Timestamp(System.currentTimeMillis() - index * 1000L));
            }

            @Override
            public int getBatchSize() {
                return postCount * commentsPerPost * repliesPerComment;
            }
        });
    }
}
