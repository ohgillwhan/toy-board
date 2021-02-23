package kr.sooragenius.toy.board.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class PostRequestDTO {
    @Data
    @NoArgsConstructor @Builder
    @AllArgsConstructor
    public static class CreateDTO {
        @NotEmpty
        private String title;
        @NotEmpty
        private String contents;
        @NotEmpty
        private String password;

        private List<PostFileRequestDTO.CreateDTO> files = new ArrayList<>();

        public void passwordEncode(PasswordEncoder passwordEncoder) {
            this.password = passwordEncoder.encode(password);
        }
    }

    @Data
    @NoArgsConstructor @Builder
    @AllArgsConstructor
    public static class UpdateDTO {
        @NotEmpty
        private String title;
        @NotEmpty
        private String contents;
    }
}
