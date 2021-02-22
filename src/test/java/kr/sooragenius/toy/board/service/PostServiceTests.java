package kr.sooragenius.toy.board.service;


import kr.sooragenius.toy.board.config.EncryptConfiguration;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.request.PostFileRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.PostFileResponseDTO;
import kr.sooragenius.toy.board.dto.response.PostResponseDTO;
import kr.sooragenius.toy.board.repository.PostFileRepository;
import kr.sooragenius.toy.board.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
@ExtendWith({SpringExtension.class})
@Import(EncryptConfiguration.class)
public class PostServiceTests {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mock
    private PostRepository postRepository;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(passwordEncoder, postRepository);
    }

    @Test
    public void 패스워드_인코더는_존재해야_하며_Bcrypt여야_한다() {
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }



    @Test
    public void 게시글_추가_암호화_확인() {
        // given
        String originalPassword = "Password";
        PostRequestDTO.Create create = new PostRequestDTO.Create("Title", "Contents", originalPassword, null);
        given(postRepository.save(any())).willAnswer(returnsFirstArg());
        // when
        PostResponseDTO.Create responseCreate = postService.addPost(create);
        // then
        assertThat(responseCreate.getTitle())
                .isEqualTo(create.getTitle());
        assertThat(responseCreate.getContents())
                .isEqualTo(create.getContents());
        assertThat(passwordEncoder.matches(originalPassword, responseCreate.getPassword())).isTrue();
    }

    @Test
    public void 게시글_추가시_첨부파일도_같이() {
        // given
        final long postId = 1L;
        List<PostFileRequestDTO.Create> files = Arrays.asList(
                new PostFileRequestDTO.Create("Original_1", "Stored_1"),
                new PostFileRequestDTO.Create("Original_2", "Stored_2"),
                new PostFileRequestDTO.Create("Original_3", "Stored_3"),
                new PostFileRequestDTO.Create("Original_4", "Stored_4"),
                new PostFileRequestDTO.Create("Original_5", "Stored_5")
        );
        PostRequestDTO.Create create = new PostRequestDTO.Create("Title", "Contents", "Password", files);
        given(postRepository.save(any())).willAnswer(item -> {
            Post post = item.getArgument(0);
            ReflectionTestUtils.setField(post, "id", postId);
            return post;
        });
        // when
        PostResponseDTO.Create addedPost = postService.addPost(create);
        // then
        assertThat(addedPost.getFiles().size()).isEqualTo(files.size());
        for(PostFileResponseDTO.Create file : addedPost.getFiles()) {
            assertThat(file.getPostId()).isEqualTo(postId);
        }
    }
}
