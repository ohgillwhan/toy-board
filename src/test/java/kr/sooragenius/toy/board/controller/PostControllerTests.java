package kr.sooragenius.toy.board.controller;

import com.google.gson.Gson;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.CommentResponseDTO;
import kr.sooragenius.toy.board.dto.response.PostResponseDTO;
import kr.sooragenius.toy.board.exception.InvalidPasswordException;
import kr.sooragenius.toy.board.service.CommentService;
import kr.sooragenius.toy.board.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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
    @MockBean
    private CommentService commentService;

    @Captor
    ArgumentCaptor<PostRequestDTO.CreateDTO> createDTOArgumentCaptor;

    @Test
    @DisplayName("리스트를 성공적으로 가져올 때")
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
    @DisplayName("글 쓰기를 성공하면 상세보기로 리다이렉트 되어야 한다.")
    public void 글_쓰기_그리고_완료후_상세보기로_리다이렉트() throws Exception {
        PostRequestDTO.CreateDTO createDTO = new PostRequestDTO.CreateDTO("TITLE", "CONTENTS", "PASSWORD", new ArrayList<>());

        given(postService.addPost(any(), any(), any())).willAnswer((item) -> {
            PostRequestDTO.CreateDTO request = item.getArgument(0);

            PostResponseDTO.Create response = PostResponseDTO.Create.of(
                    Post.create(request, "IP", "NAME"),
                    new ArrayList<>()
            );
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", createDTO.getTitle())
                        .param("password", createDTO.getPassword())
                        .param("contents", createDTO.getContents())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/posts/1"));

        // 첨부파일 업로드 할 때 DTO로 잘 변경이 되었는지 확인.
        verify(postService)
                .addPost(createDTOArgumentCaptor.capture(), any(), any());

        PostRequestDTO.CreateDTO value = createDTOArgumentCaptor.getValue();

        assertThat(value.getFiles().size()).isEqualTo(3);
        assertThat(value.getFiles().get(0).getOriginalName()).isEqualTo(firstFile.getOriginalFilename());
        assertThat(value.getFiles().get(1).getOriginalName()).isEqualTo(secondFile.getOriginalFilename());
        assertThat(value.getFiles().get(2).getOriginalName()).isEqualTo(thirdFile.getOriginalFilename());

    }

    @Test
    @DisplayName("게시글 상세보기를 할 때 조회수도 같이 증가 해야한다. ")
    public void 글_상세보기() throws Exception {
        final Long postId = 1L;
        PostResponseDTO.ViewDTO result = new PostResponseDTO.ViewDTO(1L, "TITLE", "CONTENTS", new ArrayList<>());
        List<CommentResponseDTO.ViewDTO> comments = Arrays.asList(
                new CommentResponseDTO.ViewDTO(1L, 1L, postId, "Hello"),
                new CommentResponseDTO.ViewDTO(2L, 2L, postId, "Hello"),
                new CommentResponseDTO.ViewDTO(2L, 3L, postId, "Hello"),
                new CommentResponseDTO.ViewDTO(3L, 4L, postId, "Hello"),
                new CommentResponseDTO.ViewDTO(5L, 5L, postId, "Hello"),
                new CommentResponseDTO.ViewDTO(4L, 6L, postId, "Hello")
        );
        /**
         * 0| 1L
         * 1| 2L
         * 2|    3L
         * 3|        4L
         * 4|            6L
         * 5| 5L
         */

        given(postService.findById(postId))
                .willReturn(result);
        given(commentService.findAllByPostId(postId))
                .willReturn(comments);

        MvcResult mvcResult = mockMvc.perform(
                get("/posts/1")
        )
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", result))
                .andReturn();

        // 코멘트 정렬 검증
        CommentResponseDTO.ViewDTOCollection collection = (CommentResponseDTO.ViewDTOCollection) mvcResult.getModelAndView().getModelMap().get("commentsCollection");
        List<Map.Entry<CommentResponseDTO.ViewDTO, Integer>> sort = collection.sort();
        assertComment(sort, 0, 1L, 1);
        assertComment(sort, 1, 2L, 1);
        assertComment(sort, 2, 3L, 2);
        assertComment(sort, 3, 4L, 3);
        assertComment(sort, 4, 6L, 4);
        assertComment(sort, 5, 5L, 1);

        verify(postService, times(1))
                .increaseHit(postId);
    }

    private void assertComment(List<Map.Entry<CommentResponseDTO.ViewDTO, Integer>> sort,
                             int index,
                             Long exceptCommentId,
                             int exceptLevel
                             ) {

        CommentResponseDTO.ViewDTO viewDTO = sort.get(index).getKey();
        int level = sort.get(index).getValue();

        assertThat(viewDTO.getCommentId()).isEqualTo(exceptCommentId);
        assertThat(level).isEqualTo(exceptLevel);
    }
}
