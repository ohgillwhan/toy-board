package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.request.CommentRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTests {
    Post post;

    @BeforeEach
    public void setUp() {
        post = Post.create(PostRequestDTO.Create.builder().build());
    }
    @Test
    public void 코멘트는_게시글_밑에_등록이_되어야_한다() {
        // given
        CommentRequestDTO.Create build = new CommentRequestDTO.Create(1L, "Contents", "Password");

        // when
        Comment comment = Comment.create(build, post);
        // then
        assertThat(comment.getPost()).isEqualTo(post);
    }
    @Test
    public void 비밀번호는_한번_설정하면은_변경이_불가능하다() {
        // given
        CommentRequestDTO.Create build = new CommentRequestDTO.Create(1L, "Contents", "Password");
        // when
        Comment comment = Comment.create(build, post);
        // then
        assertThat(comment.getContents()).isEqualTo(build.getContents());
        assertThat(comment.getPassword()).isEqualTo(build.getPassword());
    }
    @Test
    public void 코멘트_밑에_코멘트가_작성이_가능하다() {
        // given
        CommentRequestDTO.Create parentCommentCreateDTO = new CommentRequestDTO.Create(1L, "Parents", "Parents");
        CommentRequestDTO.Create childCommentCreateDTO = new CommentRequestDTO.Create(1L, "Child", "Child");

        Comment parentComment = Comment.create(parentCommentCreateDTO, post);
        // when
        Comment childComment = Comment.create(childCommentCreateDTO, post, parentComment);
        // then
        assertThat(childComment.getParent()).isEqualTo(parentComment);
    }
    @Test
    public void 수정을_통해서는_내용만_변경이_가능하다() {
        // given
        CommentRequestDTO.Create create =  new CommentRequestDTO.Create(1L, "Contents", "Password");
        Comment comment = Comment.create(create, post);
        CommentRequestDTO.Update update = new CommentRequestDTO.Update("NEW_CONTENTS");

        // when
        comment.update(update);

        // then
        assertThat(comment.getContents()).isEqualTo(update.getContents());
        assertThat(comment.getPassword()).isEqualTo(create.getPassword());

    }
}
