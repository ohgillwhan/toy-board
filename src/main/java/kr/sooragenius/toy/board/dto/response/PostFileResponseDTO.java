package kr.sooragenius.toy.board.dto.response;

import kr.sooragenius.toy.board.domain.PostFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PostFileResponseDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private Long postId;
        private String originalName;
        private String storedName;

        public static CreateDTO of(PostFile postFile) {
            CreateDTO createDTO = new CreateDTO();
            createDTO.postId = postFile.getPost().getId();
            createDTO.originalName = postFile.getOriginalName();
            createDTO.storedName = postFile.getStoredName();

            return createDTO;
        }
    }
}
