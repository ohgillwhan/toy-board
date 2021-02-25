package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.config.QueryDSLConfig;
import kr.sooragenius.toy.board.domain.Comment;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.request.CommentRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@Slf4j
@Import(QueryDSLConfig.class)
public class CommentRepositoryTests {

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
        createDTO = new PostRequestDTO.CreateDTO("TITLE", "CONTENTS", "PASSWORD", new ArrayList<>());
        post = Post.create(createDTO, "IP","NAME");

        commentRepository.deleteAll();
        postRepository.deleteAll();
    }


    @Test
    @Transactional
    @DisplayName("댓글은 게시글 밑에 등록이 되어야 한다.")
    public void 댓글은_게시글_밑에_등록이_되어야_한다() {
        // given
        Post savePost = postRepository.save(post);
        List<Comment> comments = Arrays.asList(
                Comment.create(new CommentRequestDTO.CreateDTO(savePost.getId(), "CONTENTS_1", "PASSWORD_1"), savePost),
                Comment.create(new CommentRequestDTO.CreateDTO(savePost.getId(), "CONTENTS_2", "PASSWORD_2"), savePost),
                Comment.create(new CommentRequestDTO.CreateDTO(savePost.getId(), "CONTENTS_3", "PASSWORD_3"), savePost)
        );
        // when
        comments.stream().forEach(commentRepository::save);

        flushAndClear();
        // then
        List<Comment> result = commentRepository.findByPostId(savePost.getId());

        assertThat(result.size()).isEqualTo(comments.size());
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("createCommentWithChildren")
    public void 코멘트_밑에는_코멘트가_등록이_가능하다(Map.Entry<CommentRequestDTO.CreateDTO, List<CommentRequestDTO.CreateDTO>> entry) {
        // given
        Post savePost = postRepository.save(post);
        Comment parent = Comment.create(entry.getKey(), savePost);
        final Comment savedParent = commentRepository.save(parent);

        long parentId = savedParent.getId();
        // when
        entry.getValue()
                .stream()
                .map(item -> Comment.create(item, post, savedParent))
                .forEach(commentRepository::save);

        flushAndClear();
        // then
        List<Comment> children = commentRepository.findByParentIdWithoutSelf(parentId);

        assertThat(children.size()).isEqualTo(entry.getValue().size());
        log.info("=>" + savedParent.toString());
        for(Comment child : children) {
            assertThat(child.getPost().getId()).isEqualTo(savePost.getId());
            assertThat(child.getParent().getId()).isEqualTo(parentId);
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
