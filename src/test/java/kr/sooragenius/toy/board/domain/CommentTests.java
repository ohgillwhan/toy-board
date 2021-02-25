package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.request.CommentRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTests {
    Post post;
    CommentRequestDTO.CreateDTO firstCommentDTO;

    @BeforeEach
    public void setUp() {
        post = Post.create(PostRequestDTO.CreateDTO.builder().build(), "IP", "NAME");
        firstCommentDTO = new CommentRequestDTO.CreateDTO(1L, "Contents", "Password");
    }

    @Test
    @DisplayName("코멘트는 게시글 밑에 등록이 되어야 한다.")
    public void 코멘트는_게시글_밑에_등록이_되어야_한다() {
        // when
        Comment comment = Comment.create(firstCommentDTO, post);
        // then
        assertThat(comment.getPost()).isEqualTo(post);
    }
    @Test
    @DisplayName("비밀번호는 한번 설정하면 변경이 불가능하다.")
    public void 비밀번호는_한번_설정하면은_변경이_불가능하다() {
        // when
        Comment comment = Comment.create(firstCommentDTO, post);
        // then
        assertThat(comment.getContents()).isEqualTo(firstCommentDTO.getContents());
        assertThat(comment.getPassword()).isEqualTo(firstCommentDTO.getPassword());
    }
    @Test
    @DisplayName("코멘트 밑에 코멘트 작성이 가능하다.")
    public void 코멘트_밑에_코멘트가_작성이_가능하다() {
        // given
        CommentRequestDTO.CreateDTO secondDTO = new CommentRequestDTO.CreateDTO(1L, "Child", "Child");

        Comment parentComment = Comment.create(firstCommentDTO, post);

        // when
        Comment childComment = Comment.create(secondDTO, post, parentComment);

        // then
        assertThat(childComment.getParent()).isEqualTo(parentComment);
    }
    @Test
    @DisplayName("수정을 통해서는 내용만 변경이 가능하다.")
    public void 수정을_통해서는_내용만_변경이_가능하다() {
        // given
        CommentRequestDTO.UpdateDTO updateDTO = new CommentRequestDTO.UpdateDTO("NEW_CONTENTS");

        Comment comment = Comment.create(firstCommentDTO, post);
        // when
        comment.update(updateDTO);

        // then
        assertThat(comment.getContents()).isEqualTo(updateDTO.getContents());
        assertThat(comment.getPassword()).isEqualTo(firstCommentDTO.getPassword());
    }
}
