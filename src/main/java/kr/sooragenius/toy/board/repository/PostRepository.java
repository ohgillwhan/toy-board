package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
