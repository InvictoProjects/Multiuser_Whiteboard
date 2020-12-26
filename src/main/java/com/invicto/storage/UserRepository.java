package com.invicto.storage;

import com.invicto.domain.User;
import com.invicto.domain.UserType;

public interface UserRepository {

    void save(User user);
    void updateLogin(User user, String login);
    void updateType(User user, UserType userType);
    void updatePermissions(User user, boolean wPermission, boolean dPermission);
    void delete(User user);
    User findById(int id);
    boolean existsById(int id);
}