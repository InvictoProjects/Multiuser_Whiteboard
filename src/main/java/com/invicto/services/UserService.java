package com.invicto.services;

import com.invicto.domain.User;
import com.invicto.domain.UserType;
import com.invicto.exceptions.PermissionException;
import com.invicto.storage.UserRepository;

import static com.invicto.exceptions.EntityExistsException.userAlreadyExists;
import static com.invicto.exceptions.EntityNotExistsException.userIsNotExist;
import static com.invicto.exceptions.PermissionException.notEnoughPermission;

public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User newUser) {
        if (userRepository.existsById(newUser.getId())) {
            throw userAlreadyExists(newUser);
        }
        userRepository.save(newUser);
    }

    public void delete(User deleteUser) {
        if (!userRepository.existsById(deleteUser.getId())) {
            throw userIsNotExist(deleteUser);
        }
        userRepository.delete(deleteUser);
    }

    public void updateLogin(User caller, User editedUser, String login) throws PermissionException {
        if (caller.getId() != editedUser.getId()) {
            throw notEnoughPermission(caller);
        }
        User user = userRepository.findById(editedUser.getId());
        if (user == null) {
            throw userIsNotExist(editedUser);
        } else {
            userRepository.updateLogin(editedUser, login);
        }
    }

    public void updateType(User caller, User editedUser, UserType userType) throws PermissionException {
        if (caller != null) {
            throw notEnoughPermission(caller);
        }
        User user = userRepository.findById(editedUser.getId());
        if (user == null) {
            throw userIsNotExist(editedUser);
        } else {
            userRepository.updateType(editedUser, userType);
        }
    }

    public void updatePermissions(User caller, User editedUser, boolean wPermission, boolean dPermission) throws PermissionException {
        if (caller.getUserType() != UserType.OWNER) {
            throw notEnoughPermission(caller);
        }
        User user = userRepository.findById(editedUser.getId());
        if (user == null) {
            throw userIsNotExist(editedUser);
        } else {
            userRepository.updatePermissions(editedUser, wPermission, dPermission);
        }
    }

    public User findById(int id){
        return userRepository.findById(id);
    }
}