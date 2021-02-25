package kr.sooragenius.toy.board.service;

import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.response.PostFileResponseDTO;
import kr.sooragenius.toy.board.repository.PostFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostFileService {
    public static String NOT_EXIST_FILE = "존재하지 않는 첨부파일입니다";
    private final PostFileRepository postFileRepository;

    @Transactional
    public PostFileResponseDTO.ViewDTO findById(Long id) {
        PostFile postFile = postFileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_FILE));

        return PostFileResponseDTO.ViewDTO.of(postFile);
    }
}
