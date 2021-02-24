package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.config.QueryDSLConfig;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.request.PostFileRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.PostResponseDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@Testcontainers
@DataJpaTest
@Import(QueryDSLConfig.class)
public class PostQueryDSLRepositoryTests {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostFileRepository postFileRepository;
    @Autowired
    private EntityManager entityManager;


    @Test
    @Disabled
    public void 게시글_리스트_카운트_테스트() {
        // given
        Map<Long, Long> postIdToFileSize = new HashMap<>();
        final long postId = 1L;
        for(int i = 0; i<5; i++) {
            List<PostFileRequestDTO.CreateDTO> files = new ArrayList<>();
            for(int j = 0; j<i; j++) {
                files.add(new PostFileRequestDTO.CreateDTO("Original_" + i + "_" + j, "Stored_" + i + "_"+j));
            }
            PostRequestDTO.CreateDTO createDTO = new PostRequestDTO.CreateDTO("Title", "Contents", "Password", files);

            Post post = Post.create(createDTO, "IP","NAME");
            postRepository.save(post);

            for(PostFileRequestDTO.CreateDTO file : files) {
                postFileRepository.save(PostFile.create(file, post));
            }
            postIdToFileSize.put(post.getId(), (long) files.size());
        }
        entityManager.flush();
        entityManager.clear();
        // when
        List<PostResponseDTO.ListDTO> listAll = postRepository.findListAll();
        // then
        for(PostResponseDTO.ListDTO list : listAll) {
            assertThat(postIdToFileSize.get(list.getPostId()))
                    .isEqualTo(list.getFileLength());
        }
    }
}
