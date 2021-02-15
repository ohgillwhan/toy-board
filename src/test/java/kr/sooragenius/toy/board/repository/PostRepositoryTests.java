package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.domain.Comment;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.CommentDTO;
import kr.sooragenius.toy.board.dto.PostDTO;
import kr.sooragenius.toy.board.dto.PostFileDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTests {

    @Container
    private static final MySQLContainer container = new MySQLContainer();

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager entityManager;

    private PostDTO.Create create;
    private Post post;
    @BeforeEach
    void setUp() {
        create = PostDTO.Create.builder()
                .title("TTILE")
                .contents("CONTENTS")
                .password("PASSWORD")
                .build();
        post = Post.create(create);
    }

    @Test
    public void 컨테이너_동작_테스트() {
        assertThat(container).isNotNull();
        assertThat(container.isRunning()).isTrue();
    }

    @Test
    @Transactional
    public void 게시글_등록() {
        // when
        Post save = postRepository.save(post);
        flushAndClear();
        // then
        post = postRepository.findById(save.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        assertThat(post.getId()).isGreaterThan(0);
        assertThat(post.getTitle()).isEqualTo(create.getTitle());
        assertThat(post.getContents()).isEqualTo(create.getContents());
        assertThat(post.getPassword()).isEqualTo(create.getPassword());
    }



    @Test
    @Transactional
    public void 첨부파일_등록() {
        // given
        List<PostFile> postFiles = Arrays.asList(
                PostFile.create(new PostFileDTO.Create("File1", "File1")),
                PostFile.create(new PostFileDTO.Create("File2", "File2")),
                PostFile.create(new PostFileDTO.Create("File3", "File3"))
        );

        // when
        for(PostFile postFile : postFiles) {
            post.addFile(postFile);
        }
        Post save = postRepository.save(post);
        flushAndClear();
        // then
        post = postRepository.findById(save.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        assertThat(post.getFiles().size()).isEqualTo(postFiles.size());
    }


    @Test
    @Transactional
    public void 댓글_등록() {
        // given
        List<Comment> comments = Arrays.asList(
                Comment.create(CommentDTO.Create.builder().contents("Contents_1").password("Password_1").build()),
                Comment.create(CommentDTO.Create.builder().contents("Contents_2").password("Password_2").build()),
                Comment.create(CommentDTO.Create.builder().contents("Contents_3").password("Password_3").build())
        );
        // when
        for(Comment comment : comments) {
            post.addComments(comment);
        }

        Post save = postRepository.save(post);
        flushAndClear();
        // then
        post = postRepository.findById(save.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        assertThat(post.getComments().size()).isEqualTo(comments.size());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
