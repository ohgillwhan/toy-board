package kr.sooragenius.toy.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

public class PostFileDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor
    public static class Create {
        @NotEmpty
        private String originalName;
        @NotEmpty
        private String storedName;
    }
}
