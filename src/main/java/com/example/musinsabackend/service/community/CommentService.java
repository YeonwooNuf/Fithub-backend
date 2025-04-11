package com.example.musinsabackend.service.community;

import com.example.musinsabackend.dto.community.CommentDto;
import com.example.musinsabackend.model.community.Comment;
import com.example.musinsabackend.model.community.CommunityPost;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.UserRepository;
import com.example.musinsabackend.repository.community.CommentRepository;
import com.example.musinsabackend.repository.community.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;

    public CommentDto addComment(Long userId, Long postId, String content, Long parentId) {
        User user = userRepository.findById(userId).orElseThrow();
        CommunityPost post = postRepository.findById(postId).orElseThrow();

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setCommunityPost(post);

        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId).orElseThrow();
            comment.setParent(parent);
        }

        commentRepository.save(comment);
        return CommentDto.from(comment);
    }

    public List<CommentDto> getCommentsByPost(Long postId) {
        List<Comment> comments = commentRepository.findByCommunityPost_Id(postId);
        List<CommentDto> result = new ArrayList<>();
        for (Comment c : comments) {
            result.add(CommentDto.from(c));
        }
        return result;
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment);
    }
}
