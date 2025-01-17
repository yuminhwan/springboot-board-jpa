package com.prgrms.springboard.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.springboard.user.domain.User;
import com.prgrms.springboard.user.domain.UserRepository;
import com.prgrms.springboard.user.dto.CreateUserRequest;
import com.prgrms.springboard.user.dto.UserDto;
import com.prgrms.springboard.user.exception.UserNotFoundException;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Long join(CreateUserRequest userRequest) {
        User user = userRepository.save(userRequest.toEntity());
        return user.getId();
    }
    
    public UserDto findOne(Long id) {
        return userRepository.findById(id)
            .map(UserDto::from)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}
