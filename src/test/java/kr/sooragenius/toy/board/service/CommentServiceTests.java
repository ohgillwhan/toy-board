package kr.sooragenius.toy.board.service;


import kr.sooragenius.toy.board.config.EncryptConfiguration;
import kr.sooragenius.toy.board.domain.Comment;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.request.CommentRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.CommentResponseDTO;
import kr.sooragenius.toy.board.repository.CommentRepository;
import kr.sooragenius.toy.board.repository.CommentRepositoryTests;
import kr.sooragenius.toy.board.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@Import(EncryptConfiguration.class)
public class CommentServiceTests {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    private CommentService commentService;

    @BeforeEach
    public void setUp() {
        commentService  = new CommentService(passwordEncoder, postRepository, commentRepository);
    }

    @Test
    public void 패스워드_인코더는_존재해야_하며_Bcrypt여야_한다() {
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    public void 코멘트는_암호화가_되어야_한다() {
        // given
        String originalPassword = "Password";
        Long postId = 1L;
        CommentRequestDTO.Create create = new CommentRequestDTO.Create(1L, "contents", originalPassword);
        given(postRepository.findById(postId)).willAnswer((item) -> {
            Post post = Post.create(PostRequestDTO.Create.builder().build());
            ReflectionTestUtils.setField(post, "id", postId);

            return Optional.of(post);
        });
        given(commentRepository.save(any())).willAnswer(new ReturnsArgumentAt(0));
        // when
        CommentResponseDTO.Create createResponse = commentService.addComment(create);
        // then
        assertThat(createResponse.getPostId()).isEqualTo(postId);
        assertThat(createResponse.getContents()).isEqualTo(create.getContents());
        assertThat(passwordEncoder.matches(originalPassword, createResponse.getPassword())).isTrue();
    }

    @Test
    public void 코멘트_밑에_코멘트() {
        // given
        Long postId = 1L;
        Long parentId = 1L;
        CommentRequestDTO.Create create = new CommentRequestDTO.Create(1L, parentId,"contents", "password");

        given(postRepository.findById(postId)).willAnswer((item) -> {
            Post post = Post.create(PostRequestDTO.Create.builder().build());
            ReflectionTestUtils.setField(post, "id", postId);

            return Optional.of(post);
        });
        given(commentRepository.findById(parentId)).willAnswer((item) -> {
            CommentRequestDTO.Create parent = new CommentRequestDTO.Create(postId, "PARENT", "PARENT");
            Post post = Post.create(PostRequestDTO.Create.builder().build());
            ReflectionTestUtils.setField(post, "id", postId);

            Comment comment = Comment.create(parent, post);
            ReflectionTestUtils.setField(comment, "id", parentId);

            return Optional.of(comment);
        });
        given(commentRepository.save(any())).willAnswer(new ReturnsArgumentAt(0));
        // when
        CommentResponseDTO.Create addedComment = commentService.addComment(create);
        // then
        assertThat(addedComment.getParentCommentId()).isEqualTo(parentId);
    }

    @Test
    public void 게시글이_없으면은_에러가_발생해야_한다() {
        String originalPassword = "Password";
        CommentRequestDTO.Create create = new CommentRequestDTO.Create(1L, "contents", originalPassword);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> commentService.addComment(create))
                .withMessage(CommentService.NOT_FOUND_POST_MESSAGE);
    }
}
