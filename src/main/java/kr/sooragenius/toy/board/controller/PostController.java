package kr.sooragenius.toy.board.controller;

import kr.sooragenius.toy.board.dto.request.PostFileRequestDTO;
import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
import kr.sooragenius.toy.board.dto.response.CommentResponseDTO;
import kr.sooragenius.toy.board.dto.response.PostResponseDTO;
import kr.sooragenius.toy.board.exception.InvalidPasswordException;
import kr.sooragenius.toy.board.service.CommentService;
import kr.sooragenius.toy.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;


    @Value("${kr.sooragenius.toy.board.file_store_path}")
    private String fileStorePath;
    @GetMapping(value = {"", "/"})
    public String list(ModelMap modelMap) {
        modelMap.addAttribute("lists", postService.findListAll());

        return "post/list";
    }
    @GetMapping("/{id}")
    public String view(ModelMap modelMap, @PathVariable("id") Long id) {
        postService.increaseHit(id);

        modelMap.addAttribute("result", postService.findById(id));
        modelMap.addAttribute("commentsCollection", new CommentResponseDTO.ViewDTOCollection(commentService.findAllByPostId(id)));

        return "post/view";
    }
    @GetMapping(value = "/create")
    public String createView(ModelMap modelMap) throws IOException {
        return "post/create";
    }

    @PostMapping(value = {"", "/"})
    public String create(@ModelAttribute PostRequestDTO.CreateDTO createDTO,
                         List<MultipartFile> uploadFiles,
                         HttpServletRequest request
                         ) throws IOException {
        List<PostFileRequestDTO.CreateDTO> files = new ArrayList<>();
        if(uploadFiles != null && !uploadFiles.isEmpty()) {
            for(MultipartFile file : uploadFiles) {
                if(!file.isEmpty()) {
                    String originalName = file.getOriginalFilename();
                    String newFileName = UUID.randomUUID().toString();
                    file.transferTo(new File(fileStorePath + "/" + newFileName));

                    files.add(new PostFileRequestDTO.CreateDTO(originalName, newFileName));
                }
            }
        }

        createDTO.setFiles(files);
        PostResponseDTO.Create create = postService.addPost(createDTO, request.getRemoteHost(), "ADMIN");

        return "redirect:/posts/" + create.getPostId();
    }

    @DeleteMapping(value = "/{id}")
    public String delete(PostRequestDTO.DeleteDTO deleteDTO, RedirectAttributes redirectAttributes) {
        try {
            postService.deleteById(deleteDTO);
        }catch(InvalidPasswordException ex) {
            redirectAttributes.addFlashAttribute("message", PostService.INVALID_PASSWORD);
            return "redirect:/posts/" + deleteDTO.getPostId();
        }
        return "redirect:/posts/";
    }
}
