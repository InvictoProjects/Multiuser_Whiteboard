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

    private final UserRepository userRepository;

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

    public void updateLogin(User caller, String login) {
        if (userRepository.findById(caller.getId()) != null) {
            userRepository.update(caller, login, caller.getUserType(), caller.isWritePermission(), caller.isWritePermission());
        }
    }

    public void updatePermissions(User caller, User editedUser, Room room, boolean wPermission, boolean dPermission) throws PermissionException {
        List<User> participants = room.getParticipants();
        boolean isTheSameRoom = (participants.contains(caller) && participants.contains(editedUser));
        boolean isCallerOwner = (caller.getUserType() == UserType.OWNER);
        if (!isTheSameRoom || !isCallerOwner) {
            throw notEnoughPermission(caller);
        }
        if (userRepository.findById(editedUser.getId()) != null) {
            userRepository.update(editedUser, editedUser.getLogin(), editedUser.getUserType(), wPermission, dPermission);
        }
    }

    public User findById(int id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw userIsNotExist(id);
        }
        return user;
    }
}
