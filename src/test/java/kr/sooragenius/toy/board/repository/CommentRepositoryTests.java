package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.config.QueryDSLConfig;
import kr.sooragenius.toy.board.domain.Comment;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.request.CommentRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
@Import(QueryDSLConfig.class)
public class CommentRepositoryTests {

    @Container
    private static final MySQLContainer container = new MySQLContainer();

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

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
        post = Post.create(createDTO);

        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    public void 컨테이너_동작_테스트() {
        assertThat(container).isNotNull();
        assertThat(container.isRunning()).isTrue();
    }


    @Test
    @Transactional
    public void
    댓글_등록() {
        // given
        Post savePost = postRepository.save(post);
        List<Comment> comments = Arrays.asList(
                Comment.create(CommentRequestDTO.CreateDTO.builder().contents("Contents_1").password("Password_1").build(), savePost),
                Comment.create(CommentRequestDTO.CreateDTO.builder().contents("Contents_2").password("Password_2").build(), savePost),
                Comment.create(CommentRequestDTO.CreateDTO.builder().contents("Contents_3").password("Password_3").build(), savePost)
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

    @ParameterizedTest
    @Transactional
    @MethodSource("createCommentWithChildren")
    public void 코멘트밑에_코멘트(Map.Entry<CommentRequestDTO.CreateDTO, List<CommentRequestDTO.CreateDTO>> entry) {
        // given
        Post savePost = postRepository.save(post);
        Comment parent = Comment.create(entry.getKey(), savePost);
        parent = commentRepository.save(parent);
        // when
        List<Comment> childList = new ArrayList<>();
        for(CommentRequestDTO.CreateDTO childDTO : entry.getValue()) {
            Comment child = Comment.create(childDTO, post, parent);
            child = commentRepository.save(child);
            childList.add(child);
        }

        flushAndClear();
        // then
        List<Comment> children = commentRepository.findByParentIdWithoutSelf(parent.getId());

        assertThat(children.size()).isEqualTo(entry.getValue().size());
        log.info("=>" + parent.toString());
        for(Comment child : children) {
            assertThat(child.getPost().getId()).isEqualTo(savePost.getId());
            assertThat(child.getParent().getId()).isEqualTo(parent.getId());
            log.info("====>" + child.toString());
        }


    }
    static List<Map.Entry<CommentRequestDTO.CreateDTO, List<CommentRequestDTO.CreateDTO>>> createCommentWithChildren() {
        List<Map.Entry<CommentRequestDTO.CreateDTO, List<CommentRequestDTO.CreateDTO>>> list = new ArrayList<>();

        for(int i = 0; i<3; i++) {
            CommentRequestDTO.CreateDTO key = CommentRequestDTO.CreateDTO.builder().contents("Contents_" + i).password("Password_" + i).build();
            List<CommentRequestDTO.CreateDTO> values = new ArrayList<>();
            for(int j = 0; j<=i; j++) {
                values.add(CommentRequestDTO.CreateDTO.builder().contents("CHILD_Contents_" + j).password("CHILD_Password_" + j).build());
            }
            list.add(new AbstractMap.SimpleEntry(key, values));
        }
        return list;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
