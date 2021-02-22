package kr.sooragenius.toy.board.dto.response;

import kr.sooragenius.toy.board.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

public class CommentResponseDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        private Long parentCommentId = 0L;
        private Long commentId;
        private Long postId;
        private String contents;
        private String password;

        public static Create of(Comment save) {
            Create create = new Create();
            if(save.getParent() != null) {
                create.parentCommentId = save.getParent().getId();
            }
            create.commentId = save.getId();
            create.postId = save.getPost().getId();
            create.contents = save.getContents();
            create.password = save.getPassword();

            return create;
        }
    }
}
