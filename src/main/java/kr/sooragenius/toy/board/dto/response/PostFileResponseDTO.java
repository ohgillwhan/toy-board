package kr.sooragenius.toy.board.dto.response;

import kr.sooragenius.toy.board.domain.PostFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

public class PostFileResponseDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private Long postId;
        private String originalName;
        private String storedName;

        public static Create of(PostFile postFile) {
            Create create = new Create();
            create.postId = postFile.getPost().getId();
            create.originalName = postFile.getOriginalName();
            create.storedName = postFile.getStoredName();

            return create;
        }
    }
}
