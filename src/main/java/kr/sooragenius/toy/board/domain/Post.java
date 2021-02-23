package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Post {
    private Post(){}
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    private String title;
    private String contents;
    private String password;

    private int hits = 0;
    public void view() {
        hits++;
    }

    public String getPassword() {
        return password;
    }
    public static Post create(PostRequestDTO.CreateDTO createDTO) {
        Post post = new Post();

        post.title = createDTO.getTitle();
        post.contents = createDTO.getContents();
        post.password = createDTO.getPassword();

        return post;
    }

    public void update(PostRequestDTO.UpdateDTO updateDTO) {
        this.title = updateDTO.getTitle();
        this.contents = updateDTO.getContents();
    }
}
