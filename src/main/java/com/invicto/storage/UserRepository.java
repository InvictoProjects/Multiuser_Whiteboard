package com.invicto.storage;

import com.invicto.domain.User;

public interface UserRepository {

    void save(User user);
    void update(User user);
    void delete(User user);
    User findById(String login);
}
