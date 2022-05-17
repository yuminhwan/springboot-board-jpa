package com.prgrms.springboard.post.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.springboard.post.domain.Post;
import com.prgrms.springboard.post.domain.PostRepository;
import com.prgrms.springboard.post.dto.CreatePostRequest;
import com.prgrms.springboard.post.dto.ModifyPostRequest;
import com.prgrms.springboard.post.dto.PostResponse;
import com.prgrms.springboard.post.exception.PostNotFoundExcpetion;
import com.prgrms.springboard.post.exception.UserNotHavePermission;
import com.prgrms.springboard.user.domain.User;
import com.prgrms.springboard.user.domain.UserRepository;
import com.prgrms.springboard.user.exception.UserNotFoundException;

@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Long createPost(CreatePostRequest postRequest) {
        User user = userRepository.findById(postRequest.getUserId())
            .orElseThrow(() -> new UserNotFoundException(postRequest.getUserId()));

        Post post = postRepository.save(Post.of(postRequest.getTitle(), postRequest.getContent(), user));
        return post.getId();
    }

    @Transactional(readOnly = true)
    public PostResponse findOne(Long id) {
        return postRepository.findById(id)
            .map(PostResponse::from)
            .orElseThrow(() -> new PostNotFoundExcpetion(id));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> findAll(Pageable pageable) {
        return postRepository.findAll(pageable)
            .map(PostResponse::from);
    }

    public void modifyPost(Long id, ModifyPostRequest postRequest) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new PostNotFoundExcpetion(id));

        User user = userRepository.findById(postRequest.getUserId())
            .orElseThrow(() -> new UserNotFoundException(postRequest.getUserId()));

        validateUser(post, user);

        post.changePostInformation(postRequest.getTitle(), postRequest.getContent());
    }

    private void validateUser(Post post, User user) {
        if (post.isNotSameUser(user)) {
            throw new UserNotHavePermission(user.getId());
        }
    }
}