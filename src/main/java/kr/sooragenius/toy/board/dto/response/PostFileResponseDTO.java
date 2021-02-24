package kr.sooragenius.toy.board.dto.response;

import kr.sooragenius.toy.board.domain.PostFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class PostFileResponseDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private Long postId;
        private Long fileId;
        private String originalName;
        private String storedName;

        public static CreateDTO of(PostFile postFile) {
            CreateDTO createDTO = new CreateDTO();
            createDTO.fileId = postFile.getId();
            createDTO.postId = postFile.getPost().getId();
            createDTO.originalName = postFile.getOriginalName();
            createDTO.storedName = postFile.getStoredName();

            return createDTO;
        }
    }
    @Data
    public static class ViewDTO {
        private Long fileId;
        private String originalName;
        private String storedName;

        public static ViewDTO of(PostFile postFile) {
            ViewDTO viewDTO = new ViewDTO();
            viewDTO.fileId = postFile.getId();
            viewDTO.originalName = postFile.getOriginalName();
            viewDTO.storedName = postFile.getStoredName();

            return viewDTO;
        }

        public String getOriginalNameForContentDisposition() throws UnsupportedEncodingException {
            return new String(originalName.getBytes(StandardCharsets.UTF_8), "ISO-8859-1");
        }
    }
}
