package kr.sooragenius.toy.board.dto.response;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CommentResponseDTOTest {
    @Test
    public void viewDTO_Sort() {
        List<CommentResponseDTO.ViewDTO> views = new ArrayList<>();
        views.add(new CommentResponseDTO.ViewDTO(1L, 1L, 1L, "TOP"));
        views.add(new CommentResponseDTO.ViewDTO(2L, 2L, 1L, "TOP"));
        views.add(new CommentResponseDTO.ViewDTO(3L, 3L, 1L, "TOP"));

        views.add(new CommentResponseDTO.ViewDTO(1L, 4L, 1L, "TOP"));
        views.add(new CommentResponseDTO.ViewDTO(1L, 5L, 1L, "TOP"));
        views.add(new CommentResponseDTO.ViewDTO(5L, 6L, 1L, "TOP"));
        views.add(new CommentResponseDTO.ViewDTO(6L, 7L, 1L, "TOP"));

        views.add(new CommentResponseDTO.ViewDTO(3L, 8L, 1L, "TOP"));

        //0// 1
        //1//   4
        //2//   5
        //3//       6
        //4//           7
        //5// 2
        //6// 3
        //7//   8
        CommentResponseDTO.ViewDTOCollection collection = new CommentResponseDTO.ViewDTOCollection(views);

        List<Map.Entry<CommentResponseDTO.ViewDTO, Integer>> sort = collection.sort();

        assertion(sort, 0, 1L, 1);
        assertion(sort, 1, 4L, 2);
        assertion(sort, 2, 5L, 2);
        assertion(sort, 3, 6L, 3);
        assertion(sort, 4, 7L, 4);

        assertion(sort, 5, 2L, 1);

        assertion(sort, 6, 3L, 1);
        assertion(sort, 7, 8L, 2);
    }

    private void assertion(List<Map.Entry<CommentResponseDTO.ViewDTO, Integer>> sort , int index, Long commentId, int level) {

        assertThat(sort.get(index).getKey().getCommentId()).isEqualTo(commentId);
        assertThat(sort.get(index).getValue()).isEqualTo(level);
    }
}