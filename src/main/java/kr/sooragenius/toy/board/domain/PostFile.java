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
    private long id;
    private String originalName;

    @ManyToOne
    @Setter(AccessLevel.PROTECTED)
    private Post post;

    public static PostFile create(PostFileDTO.Create create) {
        PostFile postFile = new PostFile();
        postFile.originalName = create.getOriginalName();

        return postFile;
    }

    public String getExtension() {
        if(!originalName.contains(".")) return "";
        return originalName.substring(originalName.lastIndexOf(".") + 1);
    }
}
