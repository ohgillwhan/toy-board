package kr.sooragenius.toy.board.message;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class PostMessage {

    private final String notExist = "kr.sooragenius.board.post.notExist";
    private final String invalidPassword = "kr.sooragenius.board.post.invalidPassword";

    private final MessageSource messageSource;

    public String postNotExist() {
        return messageSource.getMessage(notExist, null, Locale.getDefault());
    }

    public String invalidPassword() {
        return messageSource.getMessage(invalidPassword, null, Locale.getDefault());
    }
}
