package kr.sooragenius.toy.board.dto.response;

import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.request.PostFileRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class PostFileResponseDTOTest {
    @Test
    @DisplayName("첨부파일은 확장자를 가져올 수 있어야 한다.")
    public void 첨부파일은_확장자를_가져올_수_있어야_한다() {
        // given
        String fileName = "한글파일.pdf";
        // when
        PostFileResponseDTO.ViewDTO viewDTO = new PostFileResponseDTO.ViewDTO(1L, fileName, "NONONONONO");
        // then
        assertThat(viewDTO.getExtension()).isEqualTo("pdf");
    }
}
