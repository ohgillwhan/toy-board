package kr.sooragenius.toy.board.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@Import(EncryptConfiguration.class)
public class EncryptConfigurationTests {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void 패스워드_인코더는_BCRPYT여야_한다() {
        assertThat(passwordEncoder)
                .isInstanceOf(BCryptPasswordEncoder.class);
    }
}
