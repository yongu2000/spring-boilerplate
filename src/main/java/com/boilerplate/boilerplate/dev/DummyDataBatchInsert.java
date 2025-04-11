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
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
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
        int userCount = 500;
        int postCount = 1000;
        int commentsPerPost = 2;
        int repliesPerComment = 2;

        logExecutionTime("유저 데이터 생성", () -> insertUsers(userCount));
        logExecutionTime("게시글 생성",
            () -> insertPosts(postCount, userCount,
                commentsPerPost + commentsPerPost * repliesPerComment));
//        logExecutionTime("댓글 생성",
//            () -> insertComments(postCount, userCount, commentsPerPost));
//        logExecutionTime("대댓글 생성",
//            () -> insertReplies(postCount, commentsPerPost, repliesPerComment, userCount));

        int totalUsers = countTableRows("user");
        int totalPosts = countTableRows("post");
        int totalComments = countTableRows("comment");

        log.info("📊 데이터 삽입 결과");
        log.info("👤 Users: {}", totalUsers);
        log.info("📝 Posts: {}", totalPosts);
        log.info("💬 Comments (대댓글 포함): {}", totalComments);
    }

    private void logExecutionTime(String title, Runnable task) {
        log.info("[{}] 👉 {} 시작", LocalDateTime.now().format(formatter), title);
        long start = System.currentTimeMillis();

        task.run();

        long end = System.currentTimeMillis();
        log.info("[{}] ✅ {} 완료 ({}ms)", LocalDateTime.now().format(formatter), title, end - start);
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

    private void insertPosts(int totalCount, int userCount, int commentPerPostCount) {
        int chunkSize = 100_000;
        for (int offset = 0; offset < totalCount; offset += chunkSize) {
            int currentBatchSize = Math.min(chunkSize, totalCount - offset);
            insertPostChunk(offset, currentBatchSize, userCount, commentPerPostCount);
            log.info("✅ 게시글 {} ~ {} 삽입 완료", offset + 1, offset + currentBatchSize);
        }
    }

    private void insertPostChunk(int offset, int count, int userCount, int commentPerPostCount) {
        String sql =
            "INSERT INTO post (title, content, likes, comment_counts, view_counts, user_id, created_at, modified_at) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int index = offset + i;
                ps.setString(1, "게시글 제목 " + (index + 1));
                ps.setString(2, "이것은 " + (index + 1) + "번째 게시글의 내용입니다.");
                ps.setLong(3, 0L);
                ps.setLong(4, commentPerPostCount);
                ps.setLong(5, 0L);
                ps.setLong(6, (index % userCount) + 1);
                ps.setTimestamp(7,
                    new java.sql.Timestamp(System.currentTimeMillis() - index * 1000L));
                ps.setTimestamp(8,
                    new java.sql.Timestamp(System.currentTimeMillis() - index * 1000L));
            }

            @Override
            public int getBatchSize() {
                return count;
            }
        });
    }

    private void insertComments(int postCount, int userCount, int commentsPerPost) {
        int totalCount = postCount * commentsPerPost;
        int chunkSize = 1_000_000;

        for (int offset = 0; offset < totalCount; offset += chunkSize) {
            int currentBatchSize = Math.min(chunkSize, totalCount - offset);
            insertCommentChunk(offset, currentBatchSize, userCount, commentsPerPost);
            log.info("✅ 댓글 {} ~ {} 삽입 완료", offset + 1, offset + currentBatchSize);
        }
    }

    private void insertCommentChunk(int offset, int count, int userCount,
        int commentsPerPost) {
        String sql =
            "INSERT INTO comment (content, post_id, user_id, parent_comment_id, created_at, modified_at) "
                +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int index = offset + i;
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
            }

            @Override
            public int getBatchSize() {
                return count;
            }
        });
    }

    private void insertReplies(int postCount, int commentsPerPost, int repliesPerComment,
        int userCount) {
        int totalComments = postCount * commentsPerPost;
        int totalReplies = totalComments * repliesPerComment;
        int chunkSize = 1_000_000;

        for (int offset = 0; offset < totalReplies; offset += chunkSize) {
            int currentBatchSize = Math.min(chunkSize, totalReplies - offset);
            insertReplyChunk(offset, currentBatchSize, commentsPerPost, repliesPerComment,
                userCount);
            log.info("✅ 대댓글 {} ~ {} 삽입 완료", offset + 1, offset + currentBatchSize);
        }
    }

    private void insertReplyChunk(int offset, int count, int commentsPerPost, int repliesPerComment,
        int userCount) {
        String sql =
            "INSERT INTO comment (content, post_id, user_id, parent_comment_id, created_at, modified_at) "
                +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int index = offset + i;

                int parentCommentId = (index / repliesPerComment) + 1; // 1부터 시작
                int replyNumber = (index % repliesPerComment) + 1;
                int postId = ((parentCommentId - 1) / commentsPerPost) + 1;

                ps.setString(1, "댓글 " + parentCommentId + "에 대한 대댓글 " + replyNumber + "입니다.");
                ps.setInt(2, postId);
                ps.setInt(3, (index % userCount) + 1);
                ps.setLong(4, parentCommentId);
                ps.setTimestamp(5,
                    new java.sql.Timestamp(System.currentTimeMillis() - index * 1000L));
                ps.setTimestamp(6,
                    new java.sql.Timestamp(System.currentTimeMillis() - index * 1000L));
            }

            @Override
            public int getBatchSize() {
                return count;
            }
        });
    }

    private int countTableRows(String tableName) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName,
            Integer.class);
        return count != null ? count : 0;
    }
}
