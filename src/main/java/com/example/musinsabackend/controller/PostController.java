package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.PostDto;
import com.example.musinsabackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public String createPost(@RequestBody PostDto postDto) {
        postService.createPost(postDto);
        return "Post created successfully!";
    }

    @GetMapping("/{username}")
    public List<PostDto> getUserPosts(@PathVariable String username) {
        return postService.getUserPosts(username);
    }

    @PutMapping("/{id}")
    public String updatePost(@PathVariable Long id, @RequestBody PostDto postDto) {
        postService.updatePost(id, postDto);
        return "Post updated successfully!";
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "Post deleted successfully!";
    }
}