package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.PostFileDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostFileTests {
    @Test
    public void 첨부파일은_확장자를_가져올_수_있어야_한다() {
        // given
        String fileName = "한글파일.pdf";
        String storedName = "hangle.pdf";
        PostFileDTO.Create create = new PostFileDTO.Create(fileName, storedName);
        // when
        PostFile postFile = PostFile.create(create);
        // then
        assertThat(postFile.getExtension()).isEqualTo("pdf");
    }
    @Test
    public void 첨부파일의_확장자를_알_수_없을경우는_공백을_가져온다() {
        // given
        String fileName = "한글파일";
        String storedName = "hangle";
        PostFileDTO.Create create = new PostFileDTO.Create(fileName, storedName);
        // when
        PostFile postFile = PostFile.create(create);
        // then
        assertThat(postFile.getExtension()).isEqualTo("");
    }
}
