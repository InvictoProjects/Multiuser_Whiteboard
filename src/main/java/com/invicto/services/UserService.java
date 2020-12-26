package com.invicto.services;

import com.invicto.domain.User;
import com.invicto.domain.UserType;
import com.invicto.storage.UserRepository;

public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User newUser){
        userRepository.save(newUser);
    }

    public void delete(){}

    public void updateLogin(){}

    public void updateType(){}

    public void updatePermissions(){}

    public User findById(String login){
        return userRepository.findById(login);
    }
}
