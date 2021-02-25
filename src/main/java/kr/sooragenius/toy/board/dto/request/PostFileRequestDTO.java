package kr.sooragenius.toy.board.dto.request;

import kr.sooragenius.toy.board.dto.FileStore;
import kr.sooragenius.toy.board.dto.response.PostResponseDTO;
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

        public static CreateDTO of(FileStore fileStore) {
            CreateDTO create = new CreateDTO();
            create.originalName = fileStore.getOriginalName();
            create.storedName = fileStore.getStoreName();

            return create;
        }
    }
}
