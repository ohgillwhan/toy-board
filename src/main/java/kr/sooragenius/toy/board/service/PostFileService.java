package kr.sooragenius.toy.board.service;

import kr.sooragenius.toy.board.domain.PostFile;
import kr.sooragenius.toy.board.dto.response.PostFileResponseDTO;
import kr.sooragenius.toy.board.message.PostFileMessage;
import kr.sooragenius.toy.board.message.PostMessage;
import kr.sooragenius.toy.board.repository.PostFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostFileService {
    private final PostFileRepository postFileRepository;
    private final PostFileMessage postFileMessage;

    @Transactional
    public PostFileResponseDTO.ViewDTO findById(Long id) {
        PostFile postFile = postFileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(postFileMessage.postFileNotFound()));

        return PostFileResponseDTO.ViewDTO.of(postFile);
    }
}
