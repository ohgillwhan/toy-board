package kr.sooragenius.toy.board.service;

import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.PostResponseDTO;
import kr.sooragenius.toy.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;

    @Transactional
    public PostResponseDTO.Create addPost(PostRequestDTO.CreateDTO createDTO) {
        createDTO.passwordEncode(passwordEncoder);
        Post save = postRepository.save(Post.create(createDTO));

        List<PostFile> files = new ArrayList<>();
        if(createDTO.getFiles() != null && !createDTO.getFiles().isEmpty()) {
            files = createDTO.getFiles().stream()
                    .map(item -> PostFile.create(item, save))
                    .collect(Collectors.toList());
        }

        return PostResponseDTO.Create.of(save, files);
    }

    public List<PostResponseDTO.ListDTO> findListAll() {
        // 애매하다.. 1:N 관계인데.. 단방향 매핑이다.
        // 게시글 : 첨부파일인데
        // 고유번호 제목 첨부파일 갯수를 뽑아와야하는데 어떻게 할까..
        return postRepository.findListAll();
    }
}
