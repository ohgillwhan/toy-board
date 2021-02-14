package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.CommentDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Comment {
    private Comment(){}
    private String contents;
    private String password;
    private List<Comment> children = new ArrayList<>();

    public static Comment create(CommentDTO.Create create) {
        Comment comment = new Comment();
        comment.contents = create.getContents();
        comment.password = create.getPassword();

        return comment;
    }

    public void addChild(Comment childComment) {
        children.add(childComment);
    }

    public void update(CommentDTO.Update update) {
        this.contents = update.getContents();
    }
}
