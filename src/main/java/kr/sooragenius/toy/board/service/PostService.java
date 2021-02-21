package kr.sooragenius.toy.board.service;

import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.request.PostFileRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.PostFileResponseDTO;
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
    public PostResponseDTO.Create addPost(PostRequestDTO.Create create) {
        create.passwordEncode(passwordEncoder);
        Post save = postRepository.save(Post.create(create));

        List<PostFile> files = new ArrayList<>();
        if(!create.getFiles().isEmpty()) {
            files = create.getFiles().stream()
                    .map(item -> PostFile.create(item, save))
                    .collect(Collectors.toList());
        }

        return PostResponseDTO.Create.of(save, files);
    }
}
