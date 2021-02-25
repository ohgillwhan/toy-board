package kr.sooragenius.toy.board.service;

import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.PostResponseDTO;
import kr.sooragenius.toy.board.exception.InvalidPasswordException;
import kr.sooragenius.toy.board.message.PostMessage;
import kr.sooragenius.toy.board.repository.PostFileRepository;
import kr.sooragenius.toy.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final PostFileRepository fileRepository;
    private final PostMessage postMessage;

    public PostResponseDTO.Create addPost(PostRequestDTO.CreateDTO createDTO, String ip, String registerName) {
        createDTO.passwordEncode(passwordEncoder);
        Post save = postRepository.save(Post.create(createDTO, ip, registerName));

        List<PostFile> files = new ArrayList<>();
        if(createDTO.getFiles() != null && !createDTO.getFiles().isEmpty()) {
            files = createDTO.getFiles().stream()
                    .map(item -> PostFile.create(item, save))
                    .collect(Collectors.toList());

            files.forEach(fileRepository::save);
        }

        return PostResponseDTO.Create.of(save, files);
    }
    @Transactional(readOnly = true)
    public List<PostResponseDTO.ListDTO> findListAll() {
        return postRepository.findListAll();
    }
    @Transactional(readOnly = true)
    public PostResponseDTO.ViewDTO findById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(postMessage.postNotExist()));
        List<PostFile> files = fileRepository.findByPostId(id);

        return PostResponseDTO.ViewDTO.of(post, files);
    }
    @Transactional
    public void increaseHit(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException(postMessage.postNotExist()));
        post.view();
    }
    @Transactional
    public void deleteById(PostRequestDTO.DeleteDTO deleteDTO) {
        Post post = postRepository.findById(deleteDTO.getPostId()).orElseThrow(() -> new IllegalArgumentException(postMessage.postNotExist()));
        if(!passwordEncoder.matches(deleteDTO.getPassword(),post.getPassword())) {
            throw new InvalidPasswordException(postMessage.invalidPassword());
        }
        postRepository.deleteById(deleteDTO.getPostId());
    }
}
