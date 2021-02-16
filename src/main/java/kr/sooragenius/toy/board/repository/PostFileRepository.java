package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.domain.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    List<PostFile> findByPostId(Long id);
}
