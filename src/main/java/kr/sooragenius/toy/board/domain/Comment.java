package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.CommentDTO;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Comment {
    private Comment(){}

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private long id;

    private String contents;
    private String password;
    @ManyToOne
    @JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "COMMENT_ID")
    private Comment parent;


    public static Comment create(CommentDTO.Create create, Post post, Comment parent) {
        Comment comment = create(create, post);
        comment.parent = parent;

        return comment;
    }
    public static Comment create(CommentDTO.Create create, Post post) {
        Comment comment = new Comment();
        comment.contents = create.getContents();
        comment.password = create.getPassword();
        comment.post = post;
        comment.parent = comment;

        return comment;
    }

    public void update(CommentDTO.Update update) {
        this.contents = update.getContents();
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", contents='" + contents + '\'' +
                ", password='" + password + '\'' +
                ", post=" + post +
                ", parent=" + parent.getId() +
                '}';
    }
}
