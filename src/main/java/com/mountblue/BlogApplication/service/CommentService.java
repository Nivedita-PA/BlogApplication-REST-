package com.mountblue.BlogApplication.service;

import com.mountblue.BlogApplication.Security.CustomUserDetails;
import com.mountblue.BlogApplication.entity.Comment;
import com.mountblue.BlogApplication.entity.Post;
import com.mountblue.BlogApplication.entity.User;
import com.mountblue.BlogApplication.repository.CommentRepository;
import com.mountblue.BlogApplication.repository.PostRepository;
import com.mountblue.BlogApplication.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository,UserRepository userRepository){
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }
    public List<Comment> findCommentsPerPost(Long id){
        return commentRepository.findByPostId(id);
    }
    public Comment addComment(Long id, Comment comment){
        Post post = postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post not found"));
        Comment comment1 = new Comment();
        comment1.setComment(comment.getComment());
        comment1.setName(comment.getName());
        comment1.setEmail(comment.getEmail());
        comment1.setPost(post);
        comment1.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment1);
    }

    public User sendAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof CustomUserDetails) {
            username = ((CustomUserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByName(username).orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public Comment updateComment(Long id,Comment comment){
            Comment comment1 = commentRepository.findById(id).orElseThrow(()->new RuntimeException("Comment not found!"));
            if(!sendAuthenticatedUser().getName().equals(comment1.getPost().getAuthor()))
                throw new RuntimeException("User unauthorized!!");
            comment1.setName(comment.getName());
            comment1.setEmail(comment.getEmail());
            comment1.setComment(comment.getComment());
            comment1.setUpdatedAt(LocalDateTime.now());
            return commentRepository.save(comment1);
    }

    public void deleteById(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(()->new RuntimeException("Comment not found"));
        if(!sendAuthenticatedUser().getName().equals(comment.getPost().getAuthor()))
            throw new RuntimeException("User unauthorized!!");
        commentRepository.delete(comment);
    }

}
