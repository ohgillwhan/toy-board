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
        private Long postId;
        private String title;
        private String contents;
        private String password;

        private List<PostFileResponseDTO.CreateDTO> files;

        public static Create of(Post post, List<PostFile> files) {
            Create create = new Create();
            create.postId = post.getId();
            create.title = post.getTitle();
            create.contents = post.getContents();
            create.password = post.getPassword();

            if(files != null && !files.isEmpty()) {
                create.files = files.stream()
                        .map(PostFileResponseDTO.CreateDTO::of)
                        .collect(Collectors.toList());
            }

            return create;
        }
    }
    @Data
    @AllArgsConstructor @NoArgsConstructor
    public static class ViewDTO {
        private Long postId;
        private String title;
        private String contents;

        private List<PostFileResponseDTO.CreateDTO> files;

        public static ViewDTO of(Post post, List<PostFile> files) {
            ViewDTO viewDTO = new ViewDTO();
            viewDTO.postId = post.getId();
            viewDTO.title = post.getTitle();
            viewDTO.contents = post.getContents();

            if(files != null && !files.isEmpty()) {
                viewDTO.files = files.stream()
                        .map(PostFileResponseDTO.CreateDTO::of)
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
        private int hits;
    }
}
