package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.config.QueryDSLConfig;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostFileRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
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
@Import(QueryDSLConfig.class)
public class PostFileRepositoryTests {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostFileRepository postFileRepository;

    @Autowired
    private EntityManager entityManager;

    private PostRequestDTO.CreateDTO createDTO;
    private Post post;
    @BeforeEach
    void setUp() {
        createDTO = PostRequestDTO.CreateDTO.builder()
                .title("TTILE")
                .contents("CONTENTS")
                .password("PASSWORD")
                .build();
        post = Post.create(createDTO, "IP","NAME");
    }

    @Test
    @Transactional
    @DisplayName("첨부파일은 게시글 밑에 등록이 되어야 한다")
    public void 첨부파일_등록() {
        // given
        Post save = postRepository.save(post);
        List<PostFile> postFiles = Arrays.asList(
                PostFile.create(new PostFileRequestDTO.CreateDTO("File1", "File1"), save),
                PostFile.create(new PostFileRequestDTO.CreateDTO("File2", "File2"), save),
                PostFile.create(new PostFileRequestDTO.CreateDTO("File3", "File3"), save)
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
