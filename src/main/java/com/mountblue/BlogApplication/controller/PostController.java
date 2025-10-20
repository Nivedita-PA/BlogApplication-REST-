package com.mountblue.BlogApplication.controller;
import com.mountblue.BlogApplication.DTOs.PostResponseDto;
import com.mountblue.BlogApplication.entity.Post;
import com.mountblue.BlogApplication.service.PostService;
import com.mountblue.BlogApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class PostController {
        private PostService postService;

        @Autowired
        public PostController(PostService postService){
            this.postService = postService;
        }

        @PostMapping("/post")
        public ResponseEntity<PostResponseDto> createPost(@RequestBody Post post){
            Post savedPost = postService.savePost(post);
            PostResponseDto responseDto = PostResponseDto.toDTO(savedPost);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }

        @PostMapping("/updatePost/{id}")
        public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @RequestBody Post post){
            Post updatedPost = postService.updatePost(id, post);
            PostResponseDto responseDto = PostResponseDto.toDTO(updatedPost);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }

        @DeleteMapping("/deletePost/{id}")
        public ResponseEntity<String> deletePost(@PathVariable Long id){
            postService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Post deleted successfully with id "+id);
        }

        @GetMapping("/post/{id}")
        public ResponseEntity<PostResponseDto> showPost(@PathVariable Long id){
            Post post = postService.findById(id);
            PostResponseDto postResponseDto = PostResponseDto.toDTO(post);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(postResponseDto);
        }

        @GetMapping("/posts")
        public ResponseEntity<List<PostResponseDto>> getViewOfAllPosts(){
            List<Post> postList = postService.findAllPosts();
            List<PostResponseDto> dtoList = new ArrayList<>();
            for(Post p : postList){
                dtoList.add(PostResponseDto.toDTO(p));
            }
            return ResponseEntity.status(HttpStatus.OK).body(dtoList);
        }

        @GetMapping("/postsPages/{page}")
        public ResponseEntity<Page<PostResponseDto>> getPaginatedPosts(@PathVariable int page) {
            Page<Post> postsPage = postService.getPaginatedPosts(page, 10);
            Page<PostResponseDto> dtoPages = postsPage.map(post -> PostResponseDto.toDTO(post));
            return ResponseEntity.status(HttpStatus.OK).body(dtoPages);
        }

        @GetMapping("/filterByAuthor")
        public ResponseEntity<Page<PostResponseDto>> filterByAuthor(@RequestParam(required = false) String name, @RequestParam(required = false, defaultValue = "0") int page){
            Page<Post> filteredPosts = postService.getPostsByAuthorName(name, page, 10);
            Page<PostResponseDto> dtos = filteredPosts.map(post -> PostResponseDto.toDTO(post));
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        }

        @GetMapping("/filterByPublishedAt")
        public ResponseEntity<Page<PostResponseDto>> filterByPublishedAt(@RequestParam(required = false) LocalDate date, @RequestParam(required = false, defaultValue = "0") int page){
            Page<Post> filteredPosts = postService.getPostsByPublishedDate(date,page,10);
            Page<PostResponseDto> dtos = filteredPosts.map(post -> PostResponseDto.toDTO(post));
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        }

        @GetMapping("/filterByTagName")
        public ResponseEntity<Page<PostResponseDto>> filterByTagName(@RequestParam(required = false) String tagName, @RequestParam(required = false, defaultValue = "0") int page){
            Page<Post> filteredPosts = postService.getPostsByTag(tagName,page,10);
            Page<PostResponseDto> dtos = filteredPosts.map(post -> PostResponseDto.toDTO(post));
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        }

        @GetMapping("/searchPosts")
        public ResponseEntity<Page<PostResponseDto>> getSearchPosts(
                @RequestParam(required = false) String author,
                @RequestParam(required = false) String tag,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publishedDate,
                @RequestParam(required = false) String search,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "true", required = false) boolean sortAsc
        ) {
            Page<Post> posts = postService.getPosts(author, tag, publishedDate, search, page, 10, sortAsc);
            Page<PostResponseDto> dtos = posts.map(post -> PostResponseDto.toDTO(post));
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        }

        @GetMapping("/posts/sort/asc")
        public ResponseEntity<Page<PostResponseDto>> getPostsByDateAsc(@RequestParam(defaultValue = "0") int page) {
            Page<Post> postsPage = postService.getPostsSortedByDateAsc(page, 10);
            Page<PostResponseDto> dtos = postsPage.map(post -> PostResponseDto.toDTO(post));
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        }

        @GetMapping("/posts/sort/desc")
        public ResponseEntity<Page<PostResponseDto>> getPostsByDateDesc(@RequestParam(defaultValue = "0") int page) {
            Page<Post> postsPage = postService.getPostsSortedByDateDesc(page, 10);
            Page<PostResponseDto> dtos = postsPage.map(post -> PostResponseDto.toDTO(post));
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        }

        @GetMapping("/getFilteredPosts")
        public ResponseEntity<Page<PostResponseDto>> getPostsBasedOnTagsAndAuthors(
                @RequestParam(required = false) String authors,
                @RequestParam(required = false) String tags,
                @RequestParam(required = false) String search,
                @RequestParam(defaultValue = "0") int page
        ) {
            Set<String> authorList = (authors != null && !authors.isBlank())
                    ? Arrays.stream(authors.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet())
                    : null;

            Set<String> tagList = (tags != null && !tags.isBlank())
                    ? Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet())
                    : null;

            Page<Post> posts = postService.getPostsByAuthorsAndTags(authorList, tagList, search, page, 10);
            Page<PostResponseDto> dtos = posts.map(post -> PostResponseDto.toDTO(post));
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        }
}
