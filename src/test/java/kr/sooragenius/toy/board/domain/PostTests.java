package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class PostTests {
    PostRequestDTO.CreateDTO firstPostDTO = null;
    Post firstPost = null;
    @BeforeEach
    public void setUp() {
        firstPostDTO = new PostRequestDTO.CreateDTO("TITLE", "CONTENTS", "PASSWORD", new ArrayList<>());
        firstPost = Post.create(firstPostDTO, "IP", "NAME");
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,10})
    @DisplayName("조회를하면 조회수가 늘어나야 한다.")
    public void 조회를하면_조회수가_늘어나야_한다(int loop) {
        // when
        for(int i = 0; i<loop; i++) {
            firstPost.view();
        }
        // then
        assertThat(firstPost.getHits()).isEqualTo(loop);
    }

    @Test
    @DisplayName("비밀번호는 생성시 한번만 설정이 가능해야 한다.")
    public void 비밀번호는_생성시_한번만_설정이_가능() {
        // then
        assertThat(firstPost.getPassword())
                .isEqualTo(firstPostDTO.getPassword());
    }

    @Test
    @DisplayName("업데이트를 통해서 내용과 제목만 변경이 되어야 한다.")
    public void 업데이트를_통해서_내용과_제목만_변경이_되어야_한다() {
        // given
        int hits = 1000;
        ReflectionTestUtils.setField(firstPost, "hits", hits);

        PostRequestDTO.UpdateDTO updateDTO = new PostRequestDTO.UpdateDTO("UPDATE_TITLE", "UPDATE_CONTENTS");
        // when
        firstPost.update(updateDTO);

        // then
        assertThat(firstPost.getTitle()).isEqualTo(updateDTO.getTitle());
        assertThat(firstPost.getContents()).isEqualTo(updateDTO.getContents());

        assertThat(firstPost.getHits()).isEqualTo(hits);
        assertThat(firstPost.getPassword()).isEqualTo(firstPostDTO.getPassword());
    }
}
