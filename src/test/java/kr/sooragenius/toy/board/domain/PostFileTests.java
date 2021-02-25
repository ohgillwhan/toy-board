package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostFileRequestDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PostFileTests {
    static Post post = null;
    @BeforeAll
    static void setUp() {
        PostRequestDTO.CreateDTO createDTO = new PostRequestDTO.CreateDTO("TITLE", "CONTENTS", "PASSWORD", new ArrayList<>());

        post = Post.create(createDTO, "IP","NAME");
    }


    @Test
    @DisplayName("첨부파일은 여러개가 등록이 될 수 있다.")
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
