package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPostId(Long postId);

    @Query("select c from Comment c where c.parent.id = ?1 and c.id <> c.parent.id")
    List<Comment> findByParentIdWithoutSelf(Long parentId);

    List<Comment> findAllByPostId(Long id);
}
