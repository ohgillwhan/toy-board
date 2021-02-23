package kr.sooragenius.toy.board.dto.response;

import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class PostResponseDTO {
    @Data
    public static class Create {
        private String title;
        private String contents;
        private String password;

        private List<PostFileResponseDTO.Create> files;

        public static Create of(Post post, List<PostFile> files) {
            Create create = new Create();
            create.title = post.getTitle();
            create.contents = post.getContents();
            create.password = post.getPassword();

            if(files != null && !files.isEmpty()) {
                create.files = files.stream()
                        .map(PostFileResponseDTO.Create::of)
                        .collect(Collectors.toList());
            }

            return create;
        }
    }

    public static class ViewDTO {
        private String title;
        private String contents;

        private java.util.List<PostFileResponseDTO.Create> files;

        public static ViewDTO of(Post post, List<PostFile> files) {
            ViewDTO viewDTO = new ViewDTO();
            viewDTO.title = post.getTitle();
            viewDTO.contents = post.getContents();

            if(files != null && !files.isEmpty()) {
                viewDTO.files = files.stream()
                        .map(PostFileResponseDTO.Create::of)
                        .collect(Collectors.toList());
            }

            return viewDTO;
        }
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListDTO {
        private Long postId;
        private String title;
        private Long fileLength;
    }
}
