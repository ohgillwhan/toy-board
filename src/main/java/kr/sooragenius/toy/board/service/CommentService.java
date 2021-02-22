package kr.sooragenius.toy.board.service;

import kr.sooragenius.toy.board.domain.Comment;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.dto.request.CommentRequestDTO;
import kr.sooragenius.toy.board.dto.response.CommentResponseDTO;
import kr.sooragenius.toy.board.repository.CommentRepository;
import kr.sooragenius.toy.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    public static final String NOT_FOUND_POST_MESSAGE = "존재하지 않는 게시글입니다.";
    public static final String NOT_FOUND_PARENT_COMMENT = "존재하지 않는 부모 댓글 입니다.";
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponseDTO.Create addComment(CommentRequestDTO.Create create) {
        create.setPassword(passwordEncoder.encode(create.getPassword()));

        Comment comment = createComment(create);
        Comment save = commentRepository.save(comment);

        return CommentResponseDTO.Create.of(save);
    }


    private Comment createComment(CommentRequestDTO.Create create) {
        Post post = postRepository.findById(create.getPostId()).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_POST_MESSAGE));
        if(create.hasParent()) {
            Comment parentComment = commentRepository.findById(create.getParentId()).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_PARENT_COMMENT));
            return Comment.create(create, post, parentComment);
        }

        return Comment.create(create, post);
    }
}
