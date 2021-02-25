package kr.sooragenius.toy.board.service;


import kr.sooragenius.toy.board.config.EncryptConfiguration;
import kr.sooragenius.toy.board.config.TestMessageConfiguration;
import kr.sooragenius.toy.board.domain.Comment;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.request.CommentRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.CommentResponseDTO;
import kr.sooragenius.toy.board.message.CommentMessage;
import kr.sooragenius.toy.board.message.PostMessage;
import kr.sooragenius.toy.board.repository.CommentRepository;
import kr.sooragenius.toy.board.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@Import({EncryptConfiguration.class,
        TestMessageConfiguration.class,
        CommentMessage.class,
        PostMessage.class
})
public class CommentServiceTests {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CommentMessage commentMessage;
    @Autowired
    private PostMessage postMessage;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    private CommentService commentService;

    @BeforeEach
    public void setUp() {
        commentService  = new CommentService(passwordEncoder, postRepository, commentRepository, commentMessage, postMessage);
    }

    @Test
    @DisplayName("패스워드 인코더는 존재하고, BCrypt여야 한다.")
    public void 패스워드_인코더는_존재해야_하며_Bcrypt여야_한다() {
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    @DisplayName("댓글은 암호화가 되어야 한다.")
    public void 코멘트는_암호화가_되어야_한다() {
        // given
        String originalPassword = "Password";
        Long postId = 1L;

        CommentRequestDTO.CreateDTO createDTO = new CommentRequestDTO.CreateDTO(postId, "contents", originalPassword);
        given(postRepository.findById(postId)).willAnswer((item) -> {
            Post post = Post.create(PostRequestDTO.CreateDTO.builder().build(), "IP","NAME");
            ReflectionTestUtils.setField(post, "id", postId);

            return Optional.of(post);
        });
        given(commentRepository.save(any())).willAnswer(new ReturnsArgumentAt(0));
        // when
        CommentResponseDTO.CreateDTO createResponse = commentService.addComment(createDTO);
        // then
        assertThat(createResponse.getPostId()).isEqualTo(postId);
        assertThat(createResponse.getContents()).isEqualTo(createDTO.getContents());
        assertThat(passwordEncoder.matches(originalPassword, createResponse.getPassword())).isTrue();
    }

    @Test
    @DisplayName("댓글 밑에는 댓글이 등록이 가능하다.")
    public void 코멘트_밑에_코멘트() {
        // given
        Long postId = 1L;
        Long parentId = 1L;
        CommentRequestDTO.CreateDTO createDTO = new CommentRequestDTO.CreateDTO(1L, parentId,"contents", "password");

        given(postRepository.findById(postId)).willAnswer((item) -> {
            Post post = Post.create(PostRequestDTO.CreateDTO.builder().build(), "IP","NAME");
            ReflectionTestUtils.setField(post, "id", postId);

            return Optional.of(post);
        });
        given(commentRepository.findById(parentId)).willAnswer((item) -> {
            CommentRequestDTO.CreateDTO parent = new CommentRequestDTO.CreateDTO(postId, "PARENT", "PARENT");
            Post post = Post.create(PostRequestDTO.CreateDTO.builder().build(), "IP","NAME");
            ReflectionTestUtils.setField(post, "id", postId);

            Comment comment = Comment.create(parent, post);
            ReflectionTestUtils.setField(comment, "id", parentId);

            return Optional.of(comment);
        });
        given(commentRepository.save(any())).willAnswer(new ReturnsArgumentAt(0));
        // when
        CommentResponseDTO.CreateDTO addedComment = commentService.addComment(createDTO);
        // then
        assertThat(addedComment.getParentCommentId()).isEqualTo(parentId);
    }

    @Test
    @DisplayName("댓글을 등록할 때 게시글이 없으면은 에러가 발생해야 한다.")
    public void 게시글이_없으면은_에러가_발생해야_한다() {
        CommentRequestDTO.CreateDTO createDTO = new CommentRequestDTO.CreateDTO(1L, "contents", "PASSWORD");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> commentService.addComment(createDTO))
                .withMessage(postMessage.postNotExist());
    }
}
