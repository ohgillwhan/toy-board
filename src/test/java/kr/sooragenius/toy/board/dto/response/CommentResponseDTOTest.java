package kr.sooragenius.toy.board.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CommentResponseDTOTest {
    @Test
    @DisplayName("CommentCollection의 sort는 무작위로 되어있어도 레벨에 맞게 정렬이 되어야 한다.")
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

        assertSort(sort, 0, 1L, 1);
        assertSort(sort, 1, 4L, 2);
        assertSort(sort, 2, 5L, 2);
        assertSort(sort, 3, 6L, 3);
        assertSort(sort, 4, 7L, 4);

        assertSort(sort, 5, 2L, 1);

        assertSort(sort, 6, 3L, 1);
        assertSort(sort, 7, 8L, 2);
    }

    private void assertSort(List<Map.Entry<CommentResponseDTO.ViewDTO, Integer>> sort , int index, Long commentId, int level) {

        assertThat(sort.get(index).getKey().getCommentId()).isEqualTo(commentId);
        assertThat(sort.get(index).getValue()).isEqualTo(level);
    }
}