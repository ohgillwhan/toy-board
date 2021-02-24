package kr.sooragenius.toy.board.controller;

import kr.sooragenius.toy.board.dto.response.PostFileResponseDTO;
import kr.sooragenius.toy.board.service.PostFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@Controller
@RequestMapping("/post-files")
@RequiredArgsConstructor
@Slf4j
public class PostFileController {
    private final PostFileService postFileService;

    @Value("${kr.sooragenius.toy.board.file_store_path}")
    private String fileStorePath;

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> view(HttpServletRequest request, @PathVariable("id") Long id) throws FileNotFoundException, UnsupportedEncodingException {
        // Load file as Resource
        PostFileResponseDTO.ViewDTO viewDTO = postFileService.findById(id);

        File file = new File(fileStorePath + "/" + viewDTO.getStoredName());

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + viewDTO.getOriginalNameForContentDisposition() + "\"")
                .body(resource);
    }
}
