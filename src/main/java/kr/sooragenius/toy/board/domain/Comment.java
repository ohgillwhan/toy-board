package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.CommentDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Comment {
    private Comment(){}

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String contents;
    private String password;
    @ManyToOne
    @JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
    private Post post;
    @Transient
    private List<Comment> children = new ArrayList<>();

    public static Comment create(CommentDTO.Create create, Post post) {
        Comment comment = new Comment();
        comment.contents = create.getContents();
        comment.password = create.getPassword();
        comment.post = post;

        return comment;
    }

    public void addChild(Comment childComment) {
        children.add(childComment);
    }

    public void update(CommentDTO.Update update) {
        this.contents = update.getContents();
    }
}
