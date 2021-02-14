package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.PostDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Post {
    private Post(){}

    private String title;
    private String contents;
    private String password;
    private int hits = 0;
    private List<PostFile> files = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    public void view() {
        hits++;
    }

    public String getPassword() {
        return password;
    }
    public static Post create(PostDTO.Create create) {
        Post post = new Post();

        post.title = create.getTitle();
        post.contents = create.getContents();
        post.password = create.getPassword();

        return post;
    }

    public void addFile(PostFile postFile) {
        files.add(postFile);
    }

    public void addComments(Comment comment) {
        comments.add(comment);
    }
}
