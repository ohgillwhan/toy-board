package kr.sooragenius.toy.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommentDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        private String contents;
        private String password;
    }
}
