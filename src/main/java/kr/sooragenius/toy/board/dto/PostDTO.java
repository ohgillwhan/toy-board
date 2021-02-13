package kr.sooragenius.toy.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

public class PostDTO {
    @Data
    @NoArgsConstructor @Builder
    @AllArgsConstructor
    public static class Create {
        @NotEmpty
        private String title;
        @NotEmpty
        private String contents;
        @NotEmpty
        private String password;
    }
}
