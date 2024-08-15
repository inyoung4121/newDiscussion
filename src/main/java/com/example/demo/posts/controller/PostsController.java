package com.example.demo.posts.controller;


import com.example.demo.posts.domain.Post;
import com.example.demo.posts.dto.PostDTO;
import com.example.demo.posts.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RequiredArgsConstructor
@Controller
public class PostsController {


    private final PostsService postsService;

    @GetMapping("/postwirte")
    public String postwirte(Model model) {
        model.addAttribute("postDTO", new PostDTO());
        return "postwirte";
    }

    @PostMapping("/postwirte")
    public String postwirte(@ModelAttribute("postDTO") PostDTO postDTO, Model model) {
        postsService.savePost(postDTO);
        return "main";
    }

    @PutMapping("/postupdate")
    public String postupdate(@ModelAttribute("postDTO") PostDTO postDTO, Model model) {
        postsService.updatePost(postDTO);
        return "main";
    }

}
