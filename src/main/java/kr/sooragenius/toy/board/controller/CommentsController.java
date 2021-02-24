package kr.sooragenius.toy.board.controller;

import kr.sooragenius.toy.board.dto.request.CommentRequestDTO;
import kr.sooragenius.toy.board.exception.InvalidPasswordException;
import kr.sooragenius.toy.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/post-comments")
@RequiredArgsConstructor
public class CommentsController {
    private final CommentService commentService;

    @PostMapping(value = {"", "/"})
    public String create(CommentRequestDTO.CreateDTO createDTO, RedirectAttributes redirectAttributes) {
        try {
            commentService.addComment(createDTO);
        }catch(Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            ex.printStackTrace();
            return "redirect:/posts";
        }

        return "redirect:/posts/"+createDTO.getPostId();
    }


    @DeleteMapping(value = { "/{commentId}"})
    public String delete(CommentRequestDTO.DeleteDTO createDTO, RedirectAttributes redirectAttributes) {
        try {
            commentService.deleteById(createDTO);
        }catch(InvalidPasswordException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/posts/"+createDTO.getPostId();
        }catch(Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/posts";
        }

        return "redirect:/posts/"+createDTO.getPostId();
    }
}
