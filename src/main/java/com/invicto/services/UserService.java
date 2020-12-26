package com.invicto.services;

import com.invicto.domain.Room;
import com.invicto.domain.User;
import com.invicto.domain.UserType;
import com.invicto.exceptions.PermissionException;
import com.invicto.storage.UserRepository;

import java.util.List;

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
            userRepository.update(editedUser, login, editedUser.getUserType(), editedUser.isWritePermission(), editedUser.isWritePermission());
        }
    }

    public void updatePermissions(User caller, User editedUser, Room room, boolean wPermission, boolean dPermission) throws PermissionException {
        List<User> participants = room.getParticipants();
        boolean isTheSameRoom = (participants.contains(caller) && participants.contains(editedUser));
        boolean isCallerOwner = (caller.getUserType() == UserType.OWNER);
        if (!isTheSameRoom || !isCallerOwner) {
            throw notEnoughPermission(caller);
        }
        User user = userRepository.findById(editedUser.getId());
        if (user == null) {
            throw userIsNotExist(editedUser);
        } else {
            userRepository.update(editedUser, editedUser.getLogin(), editedUser.getUserType(), wPermission, dPermission);
        }
    }

    public User findById(int id){
        return userRepository.findById(id);
    }
}
