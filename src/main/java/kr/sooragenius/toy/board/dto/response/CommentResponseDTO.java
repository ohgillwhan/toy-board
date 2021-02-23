package kr.sooragenius.toy.board.dto.response;

import kr.sooragenius.toy.board.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommentResponseDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateDTO {
        private Long parentCommentId = 0L;
        private Long commentId;
        private Long postId;
        private String contents;
        private String password;

        public static CreateDTO of(Comment save) {
            CreateDTO create = new CreateDTO();
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
