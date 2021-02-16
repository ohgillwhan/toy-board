package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.PostDTO;
import kr.sooragenius.toy.board.dto.PostFileDTO;
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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostFileRepositoryTests {

    @Container
    private static final MySQLContainer container = new MySQLContainer();

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostFileRepository postFileRepository;

    @Autowired
    private EntityManager entityManager;

    private PostDTO.Create create;
    private Post post;
    @BeforeEach
    void setUp() {
        create = PostDTO.Create.builder()
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
    public void 첨부파일_등록() {
        // given
        Post save = postRepository.save(post);
        List<PostFile> postFiles = Arrays.asList(
                PostFile.create(new PostFileDTO.Create("File1", "File1"), save),
                PostFile.create(new PostFileDTO.Create("File2", "File2"), save),
                PostFile.create(new PostFileDTO.Create("File3", "File3"), save)
        );

        // when
        for(PostFile postFile : postFiles) {
            postFileRepository.save(postFile);
        }
        flushAndClear();
        // then

        assertThat(postFileRepository.findByPostId(save.getId()).size()).isEqualTo(postFiles.size());
    }


    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
