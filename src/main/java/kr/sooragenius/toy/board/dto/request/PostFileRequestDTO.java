package kr.sooragenius.toy.board.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

public class PostFileRequestDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor
    public static class CreateDTO {
        @NotEmpty
        private String originalName;
        @NotEmpty
        private String storedName;
    }
}
