package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.PostDto;
import com.example.musinsabackend.model.Post;
import com.example.musinsabackend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public void createPost(PostDto postDto) {
        Post post = new Post();
        post.setUsername(postDto.getUsername());
        post.setContent(postDto.getContent());
        postRepository.save(post);
    }

    public List<PostDto> getUserPosts(String username) {
        return postRepository.findByUsername(username).stream().map(post -> {
            PostDto dto = new PostDto();
            dto.setId(post.getId());
            dto.setUsername(post.getUsername());
            dto.setContent(post.getContent());
            return dto;
        }).collect(Collectors.toList());
    }

    public void updatePost(Long id, PostDto postDto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setContent(postDto.getContent());
        postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}

