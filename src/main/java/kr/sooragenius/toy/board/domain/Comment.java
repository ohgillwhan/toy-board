package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.request.CommentRequestDTO;
import lombok.*;

import javax.persistence.*;

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


    public static Comment create(CommentRequestDTO.CreateDTO createDTO, Post post, Comment parent) {
        Comment comment = create(createDTO, post);
        comment.parent = parent;

        return comment;
    }
    public static Comment create(CommentRequestDTO.CreateDTO createDTO, Post post) {
        Comment comment = new Comment();
        comment.contents = createDTO.getContents();
        comment.password = createDTO.getPassword();
        comment.post = post;
        comment.parent = comment;

        return comment;
    }

    public void update(CommentRequestDTO.UpdateDTO updateDTO) {
        this.contents = updateDTO.getContents();
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
