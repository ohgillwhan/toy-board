package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.CommentDTO;
import kr.sooragenius.toy.board.dto.PostDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTests {
    @Test
    public void 코멘트는_게시글_밑에_등록이_되어야_한다() {
        // given
        Post post = Post.create(PostDTO.Create.builder().build());
        CommentDTO.Create build = CommentDTO.Create.builder().contents("Contents").password("Password").build();
        Comment comment = Comment.create(build);
        // when
        post.addComments(comment);
        // then
        assertThat(post.getComments().size()).isEqualTo(1);
    }
    @Test
    public void 비밀번호는_한번_설정하면은_변경이_불가능하다() {
        // given
        CommentDTO.Create build = CommentDTO.Create.builder().contents("Contents").password("Password").build();
        // when
        Comment comment = Comment.create(build);
        // then
        assertThat(comment.getContents()).isEqualTo(build.getContents());
        assertThat(comment.getPassword()).isEqualTo(build.getPassword());
    }
    @Test
    public void 코멘트_밑에_코멘트가_작성이_가능하다() {
        // given
        CommentDTO.Create parentCommentCreateDTO = CommentDTO.Create.builder().contents("Parents").password("Parents").build();
        CommentDTO.Create childCommentCreateDTO = CommentDTO.Create.builder().contents("Child").password("Child").build();

        Comment parentComment = Comment.create(parentCommentCreateDTO);
        Comment childComment = Comment.create(childCommentCreateDTO);
        // when
        parentComment.addChild(childComment);
        // then

        assertThat(parentComment.getChildren().size())
                .isEqualTo(1);

        Comment storedChild = parentComment.getChildren().get(0);
        assertThat(storedChild)
                .isEqualTo(childComment);
    }
    @Test
    public void 수정을_통해서는_내용만_변경이_가능하다() {
        // given
        CommentDTO.Create create = CommentDTO.Create.builder().contents("Contents").password("PASSWORD").build();
        Comment comment = Comment.create(create);
        CommentDTO.Update update = CommentDTO.Update.builder().contents("Update").build();

        // when
        comment.update(update);

        // then
        assertThat(comment.getContents()).isEqualTo(update.getContents());
        assertThat(comment.getPassword()).isEqualTo(create.getPassword());

    }
}
