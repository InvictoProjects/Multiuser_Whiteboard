package com.invicto.storage;

import com.invicto.domain.User;
import com.invicto.domain.UserType;

public interface UserRepository {

    void save(User user);
    void update(User user, String login, UserType userType, boolean wPermission, boolean dPermission);
    void delete(User user);
    User findById(int id);
    boolean existsById(int id);
}
