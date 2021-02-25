package kr.sooragenius.toy.board.message;


import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class PostFileMessage {

    private final String notExist = "kr.sooragenius.board.file.notFound";

    private final MessageSource messageSource;

    public String postFileNotFound() {
        return messageSource.getMessage(notExist, null, Locale.getDefault());
    }

}
