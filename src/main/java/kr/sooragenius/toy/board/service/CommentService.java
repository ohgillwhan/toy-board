package kr.sooragenius.toy.board.service;

import kr.sooragenius.toy.board.domain.Comment;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.request.CommentRequestDTO;
import kr.sooragenius.toy.board.dto.response.CommentResponseDTO;
import kr.sooragenius.toy.board.exception.InvalidPasswordException;
import kr.sooragenius.toy.board.repository.CommentRepository;
import kr.sooragenius.toy.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    public static final String NOT_FOUND_POST_MESSAGE = "존재하지 않는 게시글입니다.";
    public static final String NOT_FOUND_PARENT_COMMENT = "존재하지 않는 부모 댓글 입니다.";
    public static final String NOT_FOUND_COMMENT = "존재하지 않는 댓글 입니다.";
    public static final String INVALID_PASSWORD = "비밀번호가 틀렸습니다.";
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponseDTO.CreateDTO addComment(CommentRequestDTO.CreateDTO createDTO) {
        createDTO.setPassword(passwordEncoder.encode(createDTO.getPassword()));

        Comment comment = createComment(createDTO);
        Comment save = commentRepository.save(comment);

        return CommentResponseDTO.CreateDTO.of(save);
    }

    @Transactional
    public Long deleteById(CommentRequestDTO.DeleteDTO createDTO) {
        final Long commentId = createDTO.getCommentId();
        final String password = createDTO.getPassword();

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_COMMENT));
        if(!passwordEncoder.matches(password, comment.getPassword())) {
            throw new InvalidPasswordException(INVALID_PASSWORD);
        }
        commentRepository.deleteById(comment.getId());

        return comment.getId();
    }
    @Transactional(readOnly = true)
    public List<CommentResponseDTO.ViewDTO> findAllByPostId(Long id) {
        return commentRepository.findAllByPostId(id)
                .stream()
                .map(CommentResponseDTO.ViewDTO::of)
                .collect(Collectors.toList());
    }


    private Comment createComment(CommentRequestDTO.CreateDTO createDTO) {
        Post post = postRepository.findById(createDTO.getPostId()).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_POST_MESSAGE));
        if(createDTO.hasParent()) {
            Comment parentComment = commentRepository.findById(createDTO.getParentId()).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_PARENT_COMMENT));
            return Comment.create(createDTO, post, parentComment);
        }

        return Comment.create(createDTO, post);
    }

}
