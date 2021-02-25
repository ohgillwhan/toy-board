package kr.sooragenius.toy.board.controller;

import kr.sooragenius.toy.board.config.TestMessageConfiguration;
import kr.sooragenius.toy.board.exception.InvalidPasswordException;
import kr.sooragenius.toy.board.message.CommentMessage;
import kr.sooragenius.toy.board.message.PostMessage;
import kr.sooragenius.toy.board.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentsController.class)
@ExtendWith(MockitoExtension.class)
@Import({
        TestMessageConfiguration.class,
        PostMessage.class,
        CommentMessage.class
})
public class PostCommentsControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostMessage postMessage;
    @Autowired
    private CommentMessage commentMessage;

    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("댓글을 작성할 때 게시글이 없으면은 게시글 목록으로 리다이렉트 한다.")
    public void 게시글이_없으면_리다이렉트() throws Exception {
        final Long postId = 1L;
        given(commentService.addComment(any()))
                .willThrow(new IllegalArgumentException(postMessage.postNotExist()));

        mockMvc.perform(
                post("/post-comments")
                .param("postId", String.valueOf(postId))
                .param("contents", "댓글입니다")
                .param("password", "qwe123")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", postMessage.postNotExist()))
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    @DisplayName("댓글을 작성할 때 문제가 없을경우 게시글 상세보기로 이동해야 한다.")
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
    @DisplayName("댓글을 삭제할 때 비밀번호가 틀린경우 에러메세지와 함께 게시글 상세보기로 이동해야 한다.")
    public void 삭제시_비밀번호가_틀릴경우() throws Exception {
        final Long commentId = 100L;
        final Long postId = 1L;
        given(commentService.deleteById(any()))
                .willThrow(new InvalidPasswordException(commentMessage.invalidPassword()));
        mockMvc.perform(
                delete("/post-comments/"+commentId)
                        .param("postId", String.valueOf(postId))
                        .param("password", "qwe123")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", commentMessage.invalidPassword()))
                .andExpect(redirectedUrl("/posts/" + postId));
    }
}
