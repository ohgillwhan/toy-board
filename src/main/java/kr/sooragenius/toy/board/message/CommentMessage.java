package kr.sooragenius.toy.board.message;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class CommentMessage {

    private final String notExist = "kr.sooragenius.board.comment.notFound";
    private final String parentNotFound = "kr.sooragenius.board.comment.parentNotFound";
    private final String invalidPassword = "kr.sooragenius.board.comment.invalidPassword";

    private final MessageSource messageSource;

    public String parentNotExist() {
        return messageSource.getMessage(parentNotFound, null, Locale.getDefault());
    }
    public String commentNotExist() {
        return messageSource.getMessage(notExist, null, Locale.getDefault());
    }

    public String invalidPassword() {
        return messageSource.getMessage(invalidPassword, null, Locale.getDefault());
    }
}
