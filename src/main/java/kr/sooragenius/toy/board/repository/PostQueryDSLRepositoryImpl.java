package kr.sooragenius.toy.board.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QTuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.sooragenius.toy.board.domain.Post;
import kr.sooragenius.toy.board.domain.QPost;
import kr.sooragenius.toy.board.domain.QPostFile;
import kr.sooragenius.toy.board.dto.response.PostResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

public class PostQueryDSLRepositoryImpl extends QuerydslRepositorySupport implements PostQueryDSLRepository{
    private final JPAQueryFactory jpaQueryFactory;
    public PostQueryDSLRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Post.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<PostResponseDTO.ListDTO> findListAll() {
        return jpaQueryFactory.select(
                Projections.constructor(
                        PostResponseDTO.ListDTO.class,
                        QPost.post.id,
                        QPost.post.title,
                        ExpressionUtils.as(
                                JPAExpressions.select(
                                        QPostFile.postFile.count()
                                )
                                        .from(QPostFile.postFile)
                                        .where(QPostFile.postFile.post().eq(QPost.post))
                                , "fileLength"),
                        QPost.post.hits
                )
        )
                .from(QPost.post)
                .fetch();
    }
}
