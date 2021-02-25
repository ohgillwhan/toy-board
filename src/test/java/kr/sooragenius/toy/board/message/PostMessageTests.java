package kr.sooragenius.toy.board.message;

import kr.sooragenius.toy.board.config.TestMessageConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({PostMessage.class, TestMessageConfiguration.class})
public class PostMessageTests {

    @Autowired
    private PostMessage postMessage;
    @Test
    public void messageTest() {
        System.out.println(postMessage.postNotExist());
    }
}
