package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "POST")
public class Post {
    private Post(){}
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    private String title;
    private String contents;
    private String password;

    private LocalDateTime registeredDatetime;
    private String registerIp;
    private String registerName;

    private int hits = 0;
    public void view() {
        hits++;
    }

    public String getPassword() {
        return password;
    }
    public static Post create(PostRequestDTO.CreateDTO createDTO, String ip, String registerName) {
        Post post = new Post();

        post.title = createDTO.getTitle();
        post.contents = createDTO.getContents();
        post.password = createDTO.getPassword();
        post.registeredDatetime = LocalDateTime.now();
        post.registerIp = ip;
        post.registerName = registerName;

        return post;
    }

    public void update(PostRequestDTO.UpdateDTO updateDTO) {
        this.title = updateDTO.getTitle();
        this.contents = updateDTO.getContents();
    }
}
