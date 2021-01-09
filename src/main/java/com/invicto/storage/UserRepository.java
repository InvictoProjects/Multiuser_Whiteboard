package com.invicto.storage;

import com.invicto.domain.User;

public interface UserRepository {

    void save(User user);
    void update(User editedUser);
    void delete(User user);
    User findById(Integer id);
    boolean existsById(Integer id);
}
