package com.mountblue.BlogApplication.controller;

import com.mountblue.BlogApplication.DTOs.CommentResponseDto;
import com.mountblue.BlogApplication.entity.Comment;
import com.mountblue.BlogApplication.entity.Post;
import com.mountblue.BlogApplication.service.CommentService;
import com.mountblue.BlogApplication.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CommentController {

    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @PostMapping("/addComment/{id}")
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable Long id, @RequestBody Comment comment){
        Comment comment1 = commentService.addComment(id, comment);
        CommentResponseDto commentResponseDto = CommentResponseDto.toDto(comment1);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDto);
    }

    @GetMapping("/allComments/{id}")
    public ResponseEntity<List<CommentResponseDto>> viewComments(@PathVariable Long id){
        List<Comment> comments = commentService.findCommentsPerPost(id);
        List<CommentResponseDto> commentResponseDto = comments.stream().map(comment -> CommentResponseDto.toDto(comment)).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDto);
    }

    @PostMapping("/updateComment/{id}")
    public ResponseEntity<CommentResponseDto> update(@PathVariable Long id, @RequestBody Comment comment){
        Comment updatedComment = commentService.updateComment(id,comment);
        CommentResponseDto dto = CommentResponseDto.toDto(updatedComment);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @DeleteMapping("/deleteComment/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        commentService.deleteById(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Comment deleted for comment id: "+id);
    }
}
