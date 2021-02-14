package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.PostDTO;
import kr.sooragenius.toy.board.dto.PostFileDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PostTests {

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @ParameterizedTest
    @ValueSource(ints = {1,5,10})
    @DisplayName("조회를하면 조회수가 늘어나야 한다.")
    public void 조회를하면_조회수가_늘어나야_한다(int loop) {
        // given
        PostDTO.Create create = new PostDTO.Create();
        create.setPassword("");
        Post post = Post.create(create);
        // when
        for(int i = 0; i<loop; i++) {
            post.view();
        }
        // then
        assertThat(post.getHits()).isEqualTo(loop);
    }

    @Test
    @DisplayName("비밀번호는 생성시 한번만 설정이 가능해야 한다.")
    public void 비밀번호는_생성시_한번만_설정이_가능() {
        // given
        String password = "sooragenius";
        PostDTO.Create create = new PostDTO.Create();
        create.setPassword(password);

        // when
        Post post = Post.create(create);

        // then
        assertThat(post.getPassword())
                .isEqualTo(password);
    }

    @Test
    public void 업데이트를_통해서_내용과_제목만_변경이_되어야_한다() {
        // given
        PostDTO.Create create = PostDTO.Create.builder()
                .title("TITLE")
                .contents("CONTENTS")
                .password("PASSWORD").build();
        int hits = 1000;
        Post post = Post.create(create);
        ReflectionTestUtils.setField(post, "hits", hits);

        PostDTO.Update update = PostDTO.Update.builder()
                .title("UPDATE_TITLE")
                .contents("UPDATE_CONTENTS")
                .build();
        // when
        post.update(update);

        // then
        assertThat(post.getTitle()).isEqualTo(update.getTitle());
        assertThat(post.getContents()).isEqualTo(update.getContents());

        assertThat(post.getHits()).isEqualTo(hits);
        assertThat(post.getPassword()).isEqualTo(create.getPassword());
    }
}
