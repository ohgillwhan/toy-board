package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTests {

    @Container
    private static final MySQLContainer container = new MySQLContainer();

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager entityManager;

    private PostRequestDTO.Create create;
    private Post post;
    @BeforeEach
    void setUp() {
        create = PostRequestDTO.Create.builder()
                .title("TTILE")
                .contents("CONTENTS")
                .password("PASSWORD")
                .build();
        post = Post.create(create);
    }

    @Test
    public void 컨테이너_동작_테스트() {
        assertThat(container).isNotNull();
        assertThat(container.isRunning()).isTrue();
    }

    @Test
    @Transactional
    public void 게시글_등록() {
        // when
        Post save = postRepository.save(post);
        flushAndClear();
        // then
        post = postRepository.findById(save.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        assertThat(post.getId()).isGreaterThan(0);
        assertThat(post.getTitle()).isEqualTo(create.getTitle());
        assertThat(post.getContents()).isEqualTo(create.getContents());
        assertThat(post.getPassword()).isEqualTo(create.getPassword());
    }


    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
