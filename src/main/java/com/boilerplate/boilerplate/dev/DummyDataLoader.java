package com.boilerplate.boilerplate.dev;

import com.boilerplate.boilerplate.domain.post.entity.Comment;
import com.boilerplate.boilerplate.domain.post.entity.Post;
import com.boilerplate.boilerplate.domain.post.repository.CommentRepository;
import com.boilerplate.boilerplate.domain.post.repository.PostRepository;
import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import com.boilerplate.boilerplate.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DummyDataLoader implements CommandLineRunner {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        long deleteStartTime = System.currentTimeMillis();

        log.info("[{}] Data Delete Started", LocalDateTime.now().format(formatter));

//        deleteAllData();

        long deleteEndTime = System.currentTimeMillis();
        long deleteExecutionTime = deleteEndTime - deleteStartTime;

        log.info("[{}] Data Delete Finished time: {}ms",
            LocalDateTime.now().format(formatter),
            deleteExecutionTime
        );

        long startTime = System.currentTimeMillis();
        log.info("[{}] Data Insertion Started", LocalDateTime.now().format(formatter));

//        createUsers();
//        createPosts();
//        createComments();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        log.info("[{}] Data Insertion Finished time: {}ms",
            LocalDateTime.now().format(formatter),
            executionTime
        );
    }

    @Transactional
    public void deleteAllData() {
        commentRepository.deleteAllInBatch(); // 1. 댓글 먼저 삭제
        postRepository.deleteAllInBatch();    // 2. 게시글 삭제
        userRepository.deleteAllInBatch();    // 3. 유저 삭제
        System.out.println("⚠ 모든 데이터 삭제 완료 ⚠");
    }

    private void createUsers() {
        for (int i = 1; i <= 50; i++) {
            User user = User.builder()
                .email("user" + i + "@example.com")
                .username("user" + i)
                .password("password" + i)
                .name("User " + i)
                .role(Role.USER)
                .build();
            entityManager.persist(user);
            if (i % 50 == 0) { // ✅ 50개 단위로 flush
                entityManager.flush();
                entityManager.clear();
            }
        }
        System.out.println("✅ 50명의 유저 생성 완료");
    }

    private void createPosts() {
        List<User> users = userRepository.findAll();

        for (int i = 1; i <= 10000; i++) {
            User user = users.get(random.nextInt(users.size()));

            Post post = Post.builder()
                .title("게시글 제목 " + i)
                .content("이것은 " + i + "번째 게시글의 내용입니다.")
                .likes(0L)
                .commentCounts(5L)
                .user(user)
                .build();

            entityManager.persist(post);

            if (i % 1000 == 0) { // ✅ 100개 단위로 flush하여 성능 최적화
                entityManager.flush();
                entityManager.clear();
            }
        }
        System.out.println("✅ 10000개의 게시글 생성 완료");
    }

    private void createComments() {
        List<User> users = userRepository.findAll();
        List<Post> posts = postRepository.findAll();
        List<Comment> comments = new ArrayList<>();
        int count = 0;
        for (Post post : posts) {
            for (int j = 1; j <= 5; j++) {
                User user = users.get(random.nextInt(users.size()));
                count++;
                Comment comment = Comment.builder()
                    .content("이것은 " + post.getTitle() + "에 대한 " + j + "번째 댓글입니다.")
                    .post(post)
                    .user(user)
                    .parentComment(null) // 최상위 댓글
                    .build();
                entityManager.persist(comment);

                if (count % 1000 == 0) { // ✅ 100개 단위로 flush하여 성능 최적화
                    entityManager.flush();
                    entityManager.clear();
                }
            }
        }
        System.out.println("✅ 10000개의 게시글에 5개씩 총 50000개의 댓글 생성 완료");
    }
}
