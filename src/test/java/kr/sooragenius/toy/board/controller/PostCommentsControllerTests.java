package kr.sooragenius.toy.board.controller;

import kr.sooragenius.toy.board.exception.InvalidPasswordException;
import kr.sooragenius.toy.board.repository.PostRepository;
import kr.sooragenius.toy.board.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentsController.class)
@ExtendWith(MockitoExtension.class)
public class PostCommentsControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Test
    public void 게시글이_없으면_리다이렉트() throws Exception {
        final Long postId = 1L;
        given(commentService.addComment(any()))
                .willThrow(new IllegalArgumentException(CommentService.NOT_FOUND_POST_MESSAGE));

        mockMvc.perform(
                post("/post-comments")
                .param("postId", String.valueOf(postId))
                .param("contents", "댓글입니다")
                .param("password", "qwe123")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", CommentService.NOT_FOUND_POST_MESSAGE))
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    public void 모든_경우가_완벽할_경우() throws Exception {
        final Long postId = 1L;

        mockMvc.perform(
                post("/post-comments")
                        .param("postId", String.valueOf(postId))
                        .param("contents", "댓글입니다")
                        .param("password", "qwe123")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));
    }


    @Test
    public void 삭제시_비밀번호가_틀릴경우() throws Exception {
        final Long commentId = 100L;
        final Long postId = 1L;
        given(commentService.deleteById(any()))
                .willThrow(new InvalidPasswordException(CommentService.INVALID_PASSWORD));
        mockMvc.perform(
                delete("/post-comments")
                        .param("postId", String.valueOf(postId))
                        .param("commentId", String.valueOf(commentId))
                        .param("password", "qwe123")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", CommentService.INVALID_PASSWORD))
                .andExpect(redirectedUrl("/posts/" + postId));
    }
}
