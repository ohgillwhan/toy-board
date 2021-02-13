package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.PostDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

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
        assertThat(bCryptPasswordEncoder.matches(password, post.getPassword()))
                .isTrue();
    }
}
