package kr.sooragenius.toy.board.controller;

import com.google.gson.Gson;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.PostResponseDTO;
import kr.sooragenius.toy.board.exception.InvalidPasswordException;
import kr.sooragenius.toy.board.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@ExtendWith(MockitoExtension.class)
public class PostControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Captor
    ArgumentCaptor<PostRequestDTO.CreateDTO> createDTOArgumentCaptor;

    private static Gson gson = new Gson();

    @Test
    public void 리스트_가져오기() throws Exception {
        List<PostResponseDTO.ListDTO> lists = Arrays.asList(
                new PostResponseDTO.ListDTO(1L, "HELLO", 1L, 1),
                new PostResponseDTO.ListDTO(2L, "HELLO", 2L, 1),
                new PostResponseDTO.ListDTO(3L, "HELLO", 3L, 1),
                new PostResponseDTO.ListDTO(4L, "HELLO", 4L, 1)
        );
        given(postService.findListAll())
                .willReturn(lists);


        // 실행
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("lists", lists));

    }


    @Test
    public void 글_쓰기_그리고_완료후_상세보기로_리다이렉트() throws Exception {
        PostRequestDTO.CreateDTO createDTO = PostRequestDTO.CreateDTO.builder()
                .title("TITLE")
                .password("PASSWORD")
                .contents("CONTENTS")
                .build();

        given(postService.addPost(any(), any(), any())).willAnswer((item) -> {
            PostRequestDTO.CreateDTO requset = item.getArgument(0);
            PostResponseDTO.Create response = PostResponseDTO.Create.of(Post.create(requset, "IP", "NAME"), new ArrayList<>());
            response.setPostId(1L);

            return response;
        });

        MockMultipartFile firstFile = new MockMultipartFile("uploadFiles", "first.json", "application/json", "FIRST_FILE".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("uploadFiles", "second.json", "application/json", "SECOND_FILE".getBytes());
        MockMultipartFile thirdFile = new MockMultipartFile("uploadFiles", "third.json", "application/json", "THIRD_FILE".getBytes());
        // 실행
        mockMvc.perform(
                multipart("/posts")
                        .file(firstFile)
                        .file(secondFile)
                        .file(thirdFile)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", createDTO.getTitle())
                        .param("password", createDTO.getPassword())
                        .param("contents", createDTO.getContents())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/posts/1"));

        verify(postService)
                .addPost(createDTOArgumentCaptor.capture(), any(), any());

        PostRequestDTO.CreateDTO value = createDTOArgumentCaptor.getValue();

        assertThat(value.getFiles().size()).isEqualTo(3);
        assertThat(value.getFiles().get(0).getOriginalName()).isEqualTo(firstFile.getOriginalFilename());
        assertThat(value.getFiles().get(1).getOriginalName()).isEqualTo(secondFile.getOriginalFilename());
        assertThat(value.getFiles().get(2).getOriginalName()).isEqualTo(thirdFile.getOriginalFilename());

    }

    @Test
    public void 글_상세보기() throws Exception {
        PostResponseDTO.ViewDTO result = new PostResponseDTO.ViewDTO();
        result.setContents("CONTENTS");
        result.setTitle("TITLE");
        result.setFiles(new ArrayList<>());

        given(postService.findById(1L))
                .willReturn(result);

        mockMvc.perform(
                get("/posts/1")
        )
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", result));
    }
}
