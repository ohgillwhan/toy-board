package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.PostFileDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
public class PostFile {
    private PostFile() {}

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private long id;
    private String originalName;

    @ManyToOne
    @JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
    private Post post;

    public static PostFile create(PostFileDTO.Create create, Post post) {
        PostFile postFile = new PostFile();
        postFile.originalName = create.getOriginalName();
        postFile.post = post;

        return postFile;
    }

    public String getExtension() {
        if(!originalName.contains(".")) return "";
        return originalName.substring(originalName.lastIndexOf(".") + 1);
    }
}
