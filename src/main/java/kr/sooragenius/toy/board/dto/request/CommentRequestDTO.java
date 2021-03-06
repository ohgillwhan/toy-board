package kr.sooragenius.toy.board.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

public class CommentRequestDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateDTO {
        @NotEmpty
        private Long postId;
        private Long parentId = 0L;
        @NotEmpty
        private String contents;
        @NotEmpty
        private String password;

        public CreateDTO(@NotEmpty Long postId, @NotEmpty String contents, @NotEmpty String password) {
            this.postId = postId;
            this.contents = contents;
            this.password = password;
        }

        public boolean hasParent() {
            return parentId > 0;
        }
    }

    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateDTO {
        @NotEmpty
        private String contents;
    }
    @Data
    @NoArgsConstructor @AllArgsConstructor
    public static class DeleteDTO {
        private Long postId;
        private Long commentId;
        private String password;
    }
}
