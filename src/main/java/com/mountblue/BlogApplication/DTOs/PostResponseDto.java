package com.mountblue.BlogApplication.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mountblue.BlogApplication.entity.Post;
import com.mountblue.BlogApplication.entity.Tag;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class PostResponseDto {
    private String title;
    private String excerpt;
    private String content;
    private String author;

    private Long userid;

    private Set<String> tags = new HashSet<>();

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public static PostResponseDto toDTO(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setTitle(post.getTitle());
        dto.setExcerpt(post.getExcerpt());
        dto.setContent(post.getContent());
        dto.setAuthor(post.getAuthor());
        dto.setTags(convertToStringTags(post));
        dto.setUserid(post.getUser().getId());
        dto.setPublishedAt(post.getPublishedAt());
        return dto;
    }

    public static Set<String> convertToStringTags(Post post){
        Set<String> tags = new HashSet<>();
        for(Tag t : post.getTags()){
            tags.add(t.getName());
        }
        return tags;
    }
}
