package kr.sooragenius.toy.board.service;

import kr.sooragenius.toy.board.domain.Comment;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.request.CommentRequestDTO;
import kr.sooragenius.toy.board.dto.response.CommentResponseDTO;
import kr.sooragenius.toy.board.exception.InvalidPasswordException;
import kr.sooragenius.toy.board.message.CommentMessage;
import kr.sooragenius.toy.board.message.PostMessage;
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
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentMessage commentMessage;
    private final PostMessage postMessage;

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

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException(commentMessage.commentNotExist()));
        if(!passwordEncoder.matches(password, comment.getPassword())) {
            throw new InvalidPasswordException(commentMessage.invalidPassword());
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
        Post post = postRepository.findById(createDTO.getPostId()).orElseThrow(() -> new IllegalArgumentException(postMessage.postNotExist()));
        if(createDTO.hasParent()) {
            Comment parentComment = commentRepository.findById(createDTO.getParentId()).orElseThrow(() -> new IllegalArgumentException(commentMessage.parentNotExist()));
            return Comment.create(createDTO, post, parentComment);
        }

        return Comment.create(createDTO, post);
    }

}
