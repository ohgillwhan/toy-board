package kr.sooragenius.toy.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

public class CommentDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        @NotEmpty
        private String contents;
        @NotEmpty
        private String password;
    }

    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Update {
        @NotEmpty
        private String contents;
    }
}
