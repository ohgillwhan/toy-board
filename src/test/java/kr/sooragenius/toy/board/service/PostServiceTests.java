package kr.sooragenius.toy.board.service;


import kr.sooragenius.toy.board.config.EncryptConfiguration;
import kr.sooragenius.toy.board.config.TestMessageConfiguration;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.request.PostFileRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.PostFileResponseDTO;
import kr.sooragenius.toy.board.dto.response.PostResponseDTO;
import kr.sooragenius.toy.board.exception.InvalidPasswordException;
import kr.sooragenius.toy.board.message.PostMessage;
import kr.sooragenius.toy.board.repository.PostFileRepository;
import kr.sooragenius.toy.board.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

@ExtendWith({MockitoExtension.class})
@ExtendWith({SpringExtension.class})
@Import({EncryptConfiguration.class,
        TestMessageConfiguration.class,
        PostMessage.class}
)
public class PostServiceTests {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PostMessage postMessage;

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostFileRepository postFileRepository;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(passwordEncoder, postRepository, postFileRepository, postMessage);
    }

    @Test
    @DisplayName("패스워드 인코더는 존재해야 하며, Bcrypt여야 한다.")
    public void 패스워드_인코더는_존재해야_하며_Bcrypt여야_한다() {
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }



    @Test
    @DisplayName("게시글을 추가하면 암호화가 되어야 한다.")
    public void 게시글_추가_암호화_확인() {
        // given
        String originalPassword = "Password";
        PostRequestDTO.CreateDTO createDTO = new PostRequestDTO.CreateDTO("Title", "Contents", originalPassword, null);
        given(postRepository.save(any())).willAnswer(returnsFirstArg());
        // when
        PostResponseDTO.Create responseCreate = postService.addPost(createDTO, "IP","NAME");
        // then
        assertThat(responseCreate.getTitle())
                .isEqualTo(createDTO.getTitle());
        assertThat(responseCreate.getContents())
                .isEqualTo(createDTO.getContents());
        assertThat(passwordEncoder.matches(originalPassword, responseCreate.getPassword())).isTrue();
    }

    @Test
    @DisplayName("게시글을 추가할 때 첨부파일도 등록이 되어야 한다.")
    public void 게시글_추가시_첨부파일도_같이() {
        // given
        final long postId = 1L;
        List<PostFileRequestDTO.CreateDTO> files = Arrays.asList(
                new PostFileRequestDTO.CreateDTO("Original_1", "Stored_1"),
                new PostFileRequestDTO.CreateDTO("Original_2", "Stored_2"),
                new PostFileRequestDTO.CreateDTO("Original_3", "Stored_3"),
                new PostFileRequestDTO.CreateDTO("Original_4", "Stored_4"),
                new PostFileRequestDTO.CreateDTO("Original_5", "Stored_5")
        );
        PostRequestDTO.CreateDTO createDTO = new PostRequestDTO.CreateDTO("Title", "Contents", "Password", files);
        given(postRepository.save(any())).willAnswer(item -> {
            Post post = item.getArgument(0);
            ReflectionTestUtils.setField(post, "id", postId);
            return post;
        });
        // when
        PostResponseDTO.Create addedPost = postService.addPost(createDTO, "IP","NAME");
        // then
        assertThat(addedPost.getFiles().size()).isEqualTo(files.size());
        for(PostFileResponseDTO.CreateDTO file : addedPost.getFiles()) {
            assertThat(file.getPostId()).isEqualTo(postId);
        }
    }

    @Test
    public void 상세보기() {
        // given
        final long postId = 1L;
        List<PostFileRequestDTO.CreateDTO> files = Arrays.asList(
                new PostFileRequestDTO.CreateDTO("Original_1", "Stored_1"),
                new PostFileRequestDTO.CreateDTO("Original_2", "Stored_2"),
                new PostFileRequestDTO.CreateDTO("Original_3", "Stored_3"),
                new PostFileRequestDTO.CreateDTO("Original_4", "Stored_4"),
                new PostFileRequestDTO.CreateDTO("Original_5", "Stored_5")
        );
        PostRequestDTO.CreateDTO createDTO = new PostRequestDTO.CreateDTO("Title", "Contents", "Password", files);
        Post post = Post.create(createDTO, "IP", "NAME");

        given(postRepository.findById(1L)).willAnswer(item -> {
            ReflectionTestUtils.setField(post, "id", postId);
            return Optional.of(post);
        });
        given(postFileRepository.findByPostId(1L))
                .willAnswer(item -> files.stream()
                        .map(file -> PostFile.create(file, post))
                        .collect(Collectors.toList()));

        // when
        PostResponseDTO.ViewDTO result = postService.findById(1L);

        // then
        assertThat(result.getPostId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo(createDTO.getTitle());
        assertThat(result.getContents()).isEqualTo(createDTO.getContents());
        assertThat(result.getFiles().size()).isEqualTo(files.size());
    }



    @Test
    public void 상세보기_없을경우() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> postService.findById(1L))
                .withMessage(postMessage.postNotExist());
    }

    @Test
    public void 상세보기_하면은_조회수_증가() {
        // given
        final Long postId = 1L;
        PostRequestDTO.CreateDTO createDTO = new PostRequestDTO.CreateDTO("Title", "Contents", "Password", new ArrayList<>());
        Post post = Post.create(createDTO, "IP", "NAME");

        given(postRepository.findById(postId)).willAnswer(item -> {
            ReflectionTestUtils.setField(post, "id", postId);
            return Optional.of(post);
        });

        // when
        postService.increaseHit(postId);

        // then
        assertThat(post.getHits()).isEqualTo(1);
    }

    @Test
    public void 게시글_삭제() {
        // given
        final Long postId = 1L;
        String password = "qwer1234";
        String encryptedPassword = passwordEncoder.encode(password);

        PostRequestDTO.DeleteDTO deleteDTO = new PostRequestDTO.DeleteDTO(postId, password);

        PostRequestDTO.CreateDTO createDTO = new PostRequestDTO.CreateDTO("Title", "Contents", encryptedPassword, new ArrayList<>());
        Post post = Post.create(createDTO, "IP", "NAME");

        given(postRepository.findById(postId)).willAnswer(item -> {
            ReflectionTestUtils.setField(post, "id", postId);
            return Optional.of(post);
        });

        postService.deleteById(deleteDTO);
    }


    @Test
    public void 게시글_삭제_비밀번호_틀릴시() {
        // given
        final Long postId = 1L;
        String password = "qwer1234";
        String encryptedPassword = passwordEncoder.encode(password);

        PostRequestDTO.DeleteDTO deleteDTO = new PostRequestDTO.DeleteDTO(postId, "wrong_password");

        PostRequestDTO.CreateDTO createDTO = new PostRequestDTO.CreateDTO("Title", "Contents", encryptedPassword, new ArrayList<>());
        Post post = Post.create(createDTO, "IP", "NAME");

        given(postRepository.findById(postId)).willAnswer(item -> {
            ReflectionTestUtils.setField(post, "id", postId);
            return Optional.of(post);
        });

        assertThatExceptionOfType(InvalidPasswordException.class)
                .isThrownBy(() -> postService.deleteById(deleteDTO))
                .withMessage(postMessage.invalidPassword());
    }
}
