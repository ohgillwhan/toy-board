package kr.sooragenius.toy.board.repository;

import kr.sooragenius.toy.board.dto.response.PostResponseDTO;

import java.util.List;

public interface PostQueryDSLRepository {
    List<PostResponseDTO.ListDTO> findListAll();
}
