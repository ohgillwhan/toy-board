package kr.sooragenius.toy.board.domain;

import kr.sooragenius.toy.board.dto.PostFileDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PostFile {
    private PostFile() {}
    private String originalName;

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
