package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostFileRequestDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PostFileTests {
    static Post post = null;
    @BeforeAll
    static void setUp() {

        // given
        PostRequestDTO.CreateDTO createDTO = new PostRequestDTO.CreateDTO();
        createDTO.setPassword("");
        post = Post.create(createDTO, "IP","NAME");
    }
    @Test
    public void 첨부파일은_확장자를_가져올_수_있어야_한다() {
        // given
        String fileName = "한글파일.pdf";
        String storedName = "hangle.pdf";
        PostFileRequestDTO.CreateDTO createDTO = new PostFileRequestDTO.CreateDTO(fileName, storedName);
        // when
        PostFile postFile = PostFile.create(createDTO, post);
        // then
        assertThat(postFile.getExtension()).isEqualTo("pdf");
    }
    @Test
    public void 첨부파일의_확장자를_알_수_없을경우는_공백을_가져온다() {
        // given
        String fileName = "한글파일";
        String storedName = "hangle";
        PostFileRequestDTO.CreateDTO createDTO = new PostFileRequestDTO.CreateDTO(fileName, storedName);
        // when
        PostFile postFile = PostFile.create(createDTO, post);
        // then
        assertThat(postFile.getExtension()).isEqualTo("");
    }


    @Test
    public void 첨부파일은_여러개가_등록이_될_수_있다() {

        // when
        List<PostFile> postFiles = Arrays.asList(
                PostFile.create(new PostFileRequestDTO.CreateDTO("File1", "File1"), post),
                PostFile.create(new PostFileRequestDTO.CreateDTO("File2", "File2"), post),
                PostFile.create(new PostFileRequestDTO.CreateDTO("File3", "File3"), post)
        );

        // then
        assertThat(postFiles)
                .allMatch((item) -> item.getPost() == post);
    }
}
