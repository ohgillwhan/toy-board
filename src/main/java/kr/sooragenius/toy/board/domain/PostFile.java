package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.request.PostFileRequestDTO;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class PostFile {
    private PostFile() {}

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private long id;
    private String originalName;
    private String storedName;

    @ManyToOne
    @JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
    private Post post;

    public static PostFile create(PostFileRequestDTO.CreateDTO createDTO, Post post) {
        PostFile postFile = new PostFile();
        postFile.originalName = createDTO.getOriginalName();
        postFile.storedName = createDTO.getStoredName();
        postFile.post = post;

        return postFile;
    }

    public String getExtension() {
        if(!originalName.contains(".")) return "";
        return originalName.substring(originalName.lastIndexOf(".") + 1);
    }
}
