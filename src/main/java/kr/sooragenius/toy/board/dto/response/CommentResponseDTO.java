package kr.sooragenius.toy.board.dto.response;

import kr.sooragenius.toy.board.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommentResponseDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateDTO {
        private Long parentCommentId = 0L;
        private Long commentId;
        private Long postId;
        private String contents;
        private String password;

        public static CreateDTO of(Comment save) {
            CreateDTO create = new CreateDTO();
            if(save.getParent() != null) {
                create.parentCommentId = save.getParent().getId();
            }
            create.commentId = save.getId();
            create.postId = save.getPost().getId();
            create.contents = save.getContents();
            create.password = save.getPassword();

            return create;
        }
    }

    @Data
    @NoArgsConstructor @AllArgsConstructor
    public static class ViewDTO {
        private Long parentId;
        private Long commentId;
        private Long postId;
        private String contents;

        public static ViewDTO of(Comment comment) {
            ViewDTO viewDTO = new ViewDTO();

            viewDTO.parentId = comment.getParent().getId();
            viewDTO.commentId = comment.getId();
            viewDTO.postId = comment.getPost().getId();
            viewDTO.contents = comment.getContents();

            return viewDTO;
        }
    }
    public static class ViewDTOCollection {
        private List<ViewDTO> viewDTOList;
        public ViewDTOCollection(List<ViewDTO> viewDTOList) {
            this.viewDTOList = viewDTOList;
        }
        public boolean isEmpty() {
            if(viewDTOList == null || viewDTOList.isEmpty()) return true;
            return false;
        }
        public List<Map.Entry<ViewDTO, Integer>> sort() {
            List<Map.Entry<ViewDTO, Integer>> result = new ArrayList<>();
            List<ViewDTO> topLevelViews = getTopLevelViews();

            for(ViewDTO viewDTO : topLevelViews) {
                result.add(new AbstractMap.SimpleEntry<>(viewDTO, 1));
                findChild(result, viewDTO, 1);
            }

            return result;
        }

        private void findChild(List<Map.Entry<ViewDTO, Integer>> result, ViewDTO parent, int level) {
            for(ViewDTO viewDTO : viewDTOList) {
                if(viewDTO == parent) continue;
                if(viewDTO.getParentId() == parent.getCommentId()) {
                    result.add(new AbstractMap.SimpleEntry<>(viewDTO, level + 1));
                    findChild(result, viewDTO, level + 1);
                }
            }
        }

        private List<ViewDTO> getTopLevelViews() {
            return viewDTOList
                    .stream()
                    .filter(item -> item.getCommentId() == item.getParentId())
                    .collect(Collectors.toList());
        }
    }
}
