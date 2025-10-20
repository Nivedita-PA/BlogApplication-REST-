package com.mountblue.BlogApplication.service;
import com.mountblue.BlogApplication.Security.CustomUserDetails;
import com.mountblue.BlogApplication.entity.Post;
import com.mountblue.BlogApplication.entity.Tag;
import com.mountblue.BlogApplication.entity.User;
import com.mountblue.BlogApplication.repository.CommentRepository;
import com.mountblue.BlogApplication.repository.PostRepository;
import com.mountblue.BlogApplication.repository.TagRepository;
import com.mountblue.BlogApplication.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PostService{

        private PostRepository postRepository;
        private TagRepository tagRepository;
        private CommentRepository commentRepository;
        private UserRepository userRepository;

        @Autowired
        public PostService(PostRepository postRepository, TagRepository tagRepository, CommentRepository commentRepository,UserRepository userRepository){
           this.postRepository = postRepository;
           this.tagRepository = tagRepository;
           this.commentRepository = commentRepository;
           this.userRepository = userRepository;
        }
        public PostService(){}

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

        public Post savePost(Post post){
               if(!post.getAuthor().equals(sendAuthenticatedUser().getName()) && !sendAuthenticatedUser().isAdmin())
                   throw new RuntimeException("User doesn't match");
               Post p = new Post();
               p.setTitle(post.getTitle());
               p.setTags(addTags(post.getTags()));
               p.setExcerpt(post.getExcerpt());
               p.setContent(post.getContent());
               p.setAuthor(post.getAuthor());
               User user = userRepository.findByName(post.getAuthor()).orElseThrow(() -> new RuntimeException("User not found!"));
               p.setUser(user);
               p.setPublished(true);
               p.setPublishedAt(LocalDateTime.now());
               return postRepository.save(p);
        }

        public Post updatePost(Long id, Post updatedPost) {
           Post existingPost = postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post not found"));

           if(!sendAuthenticatedUser().getName().equals(existingPost.getAuthor())
                   && !sendAuthenticatedUser().isAdmin())
               throw new RuntimeException("User mismatch");

               existingPost.setTitle(updatedPost.getTitle());
               existingPost.getTags().clear();
               existingPost.setTags(addTags(updatedPost.getTags()));
               existingPost.setExcerpt(updatedPost.getExcerpt());
               existingPost.setContent(updatedPost.getContent());
               existingPost.setPublishedAt(updatedPost.getPublishedAt());
               existingPost.setUpdatedAt(LocalDateTime.now());
               if(sendAuthenticatedUser().isAdmin()) {
                   existingPost.setAuthor(updatedPost.getAuthor());
                   existingPost.setUser(userRepository.findByName(updatedPost.getAuthor()).get());
               }
               return postRepository.save(existingPost);
        }

        public Set<Tag> addTags(Set<Tag> tagSet){
            Set<Tag> tags = new HashSet<>();
            for(Tag tag : tagSet){
                Optional<Tag> tag1 = tagRepository.findByName(tag.getName());
                if(tag1.isPresent()) tags.add(tag1.get());
                else {
                    Tag t = new Tag();
                    t.setName(tag.getName());
                    Tag tag2 = tagRepository.save(t);
                    tags.add(tag2);
                }
            }
            return tags;
        }

        public Post findById(Long id){
           Optional<Post> existingPost = postRepository.findById(id);
           return existingPost.orElseThrow(()-> new RuntimeException("Post not found"));
        }

        @Transactional
        public void deleteById(Long id){
            Post postToBeDeleted = postRepository.findById(id).orElseThrow(()->new RuntimeException("Post isn't present"));
            if(!sendAuthenticatedUser().getName().equals(postToBeDeleted.getAuthor())
                    && !sendAuthenticatedUser().isAdmin())
                throw new RuntimeException("User unauthorized");
           commentRepository.deleteByPostId(id);
           postRepository.deleteById(id);
        }

        public List<Post> findAllPosts(){
           return postRepository.findAll();
        }

        public Page<Post> getAllPosts(Pageable pageable){
           return postRepository.findAll(pageable);
        }

        public Page<Post> getPaginatedPosts(int page, int size) {
            Pageable pageable = PageRequest.of(page, size);
            return getAllPosts(pageable);
        }

        public Page<Post> getPostsByAuthorName(String author, int page, int pageSize){
           Pageable pageable = PageRequest.of(page, pageSize);
           return postRepository.findByAuthorIgnoreCase(author,pageable);
        }

        public Page<Post> getPostsByPublishedDate(LocalDate date, int page, int pageSize){
           if(date==null) throw new RuntimeException("Enter a date");
           LocalDateTime start = date.atStartOfDay();
           LocalDateTime end = date.plusDays(1).atStartOfDay();
           Pageable pageable = PageRequest.of(page, pageSize);
           return postRepository.findByPublishedAtGreaterThanEqualAndPublishedAtLessThan(start,end,pageable);
        }
        public Page<Post> getPostsByTag(String tagName, int page, int pageSize) {
            Pageable pageable = PageRequest.of(page, pageSize);
            return postRepository.findByTags_NameAndIsPublishedTrue(tagName, pageable);
        }

        public Page<Post> getPosts(String author, String tag, LocalDate publishedDate, String search, int page, int size, boolean sortAsc) {
            LocalDateTime startOfDay = null;
            LocalDateTime endOfDay = null;
            if (publishedDate != null) {
                startOfDay = publishedDate.atStartOfDay();
                endOfDay = publishedDate.plusDays(1).atStartOfDay();
            }
            Pageable pageable = PageRequest.of(page, size, sortAsc ? Sort.by("publishedAt").ascending() : Sort.by("publishedAt").descending());
            return postRepository.findByFilters(author, tag, publishedDate, startOfDay, endOfDay, search, pageable);
        }

        public Page<Post> getPostsSortedByDateAsc(int page, int size) {
            Pageable pageable = PageRequest.of(page, size);
            return postRepository.findAllByOrderByPublishedAtAsc(pageable);
        }

        public Page<Post> getPostsSortedByDateDesc(int page, int size) {
            Pageable pageable = PageRequest.of(page, size);
            return postRepository.findAllByOrderByPublishedAtDesc(pageable);
        }

        public Page<Post> getPostsByAuthorsAndTags(Set<String> authors, Set<String> tags, String search, int page, int size) {
            if (authors != null && authors.isEmpty()) authors = null;
            if (tags != null && tags.isEmpty()) tags = null;
            Pageable pageable = PageRequest.of(page, size);
            return postRepository.findByFilters(authors, tags, search, pageable);
        }

}
