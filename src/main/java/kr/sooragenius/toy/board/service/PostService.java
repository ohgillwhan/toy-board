package kr.sooragenius.toy.board.service;

import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.PostResponseDTO;
import kr.sooragenius.toy.board.exception.InvalidPasswordException;
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
    public static String NOT_EXIST_POST = "존재하지 않는 게시글입니다.";
    public static String INVALID_PASSWORD = "비밀번호가 틀렸습니다.";

    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final PostFileRepository fileRepository;

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
        // 애매하다.. 1:N 관계인데.. 단방향 매핑이다.
        // 게시글 : 첨부파일인데
        // 고유번호 제목 첨부파일 갯수를 뽑아와야하는데 어떻게 할까..
        return postRepository.findListAll();
    }
    @Transactional(readOnly = true)
    public PostResponseDTO.ViewDTO findById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_POST));
        List<PostFile> files = fileRepository.findByPostId(id);

        return PostResponseDTO.ViewDTO.of(post, files);
    }
    @Transactional
    public void increaseHit(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_POST));
        post.view();
    }
    @Transactional
    public void deleteById(PostRequestDTO.DeleteDTO deleteDTO) {
        Post post = postRepository.findById(deleteDTO.getPostId()).orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_POST));
        if(!passwordEncoder.matches(deleteDTO.getPassword(),post.getPassword())) {
            throw new InvalidPasswordException(INVALID_PASSWORD);
        }
        postRepository.deleteById(deleteDTO.getPostId());
    }
}
