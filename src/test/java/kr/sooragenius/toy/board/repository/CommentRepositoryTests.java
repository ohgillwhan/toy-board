package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.domain.Comment;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.CommentDTO;
import kr.sooragenius.toy.board.dto.PostDTO;
import kr.sooragenius.toy.board.dto.PostFileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTests {

    @Container
    private static final MySQLContainer container = new MySQLContainer();

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

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
    public void 댓글_등록() {
        // given
        Post savePost = postRepository.save(post);
        List<Comment> comments = Arrays.asList(
                Comment.create(CommentDTO.Create.builder().contents("Contents_1").password("Password_1").build(), savePost),
                Comment.create(CommentDTO.Create.builder().contents("Contents_2").password("Password_2").build(), savePost),
                Comment.create(CommentDTO.Create.builder().contents("Contents_3").password("Password_3").build(), savePost)
        );
        // when
        for(Comment comment : comments) {
            commentRepository.save(comment);
        }

        flushAndClear();
        // then
        List<Comment> byPostId = commentRepository.findByPostId(savePost.getId());
        assertThat(byPostId.size())
                .isEqualTo(comments.size());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
