package com.invicto.storage.postgresql;

import com.invicto.domain.User;
import com.invicto.domain.UserType;
import com.invicto.storage.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepositoryImpl implements UserRepository {

    private Connector connector;

    @Override
    public void save(User user) {
        String statement = "INSERT INTO Users(login, room_id, user_type, write_permission, draw_permission) " +
                "VALUES('" + user.getLogin() + "', '" + user.getRoomId() + "', '" + user.getUserType().toString() +
                "', " + String.valueOf(user.isWritePermission()).toUpperCase() + ", " + String.valueOf(user.isDrawPermission()).toUpperCase() + ")"+
                "RETURNING id";
        ResultSet result = connector.executeQuery(statement);
        try {
            if (result.next()) {
                Integer id = result.getInt("id");
                user.setId(id);
            }
        } catch (SQLException e) {
            user.setId(null);
        }
    }

    @Override
    public void update(User editedUser) {
        String statement = "UPDATE Users SET (login, room_id, user_type, write_permission, draw_permission) " +
                "VALUES('" + editedUser.getLogin() + "', '" + editedUser.getRoomId() + "', '" + editedUser.getUserType().toString() +
                "', " + String.valueOf(editedUser.isWritePermission()).toUpperCase() + ", " + String.valueOf(editedUser.isDrawPermission()).toUpperCase() + ")" +
                "WHERE id = " + editedUser.getId();
        connector.executeUpdate(statement);
    }

    @Override
    public void delete(User user) {
        String statement = "DELETE FROM Users WHERE id = " + user.getId();
        connector.executeUpdate(statement);
    }

    @Override
    public User findById(int id) {
        String statement = "SELECT * FROM Users WHERE id = " + id;
        ResultSet result = connector.executeQuery(statement);
        if (result == null) {
            return null;
        } else {
            try {
                result.next();
                String login = result.getString("login");
                String roomId = result.getString("room_id");
                String sUserType = result.getString("user_type");
                UserType userType;
                if (sUserType.equals("room_owner")) {
                    userType = UserType.OWNER;
                } else {
                    userType = UserType.GUEST;
                }
                boolean wPermission = result.getString("write_permission").equals("TRUE");
                boolean dPermission = result.getString("draw_permission").equals("TRUE");
                return new User(id, login, roomId, userType, wPermission, dPermission);
            } catch (SQLException e) {
                return null;
            }
        }
    }

    @Override
    public boolean existsById(int id) {
        String statement = "SELECT * FROM Users WHERE id = " + id;
        ResultSet result = connector.executeQuery(statement);
        try {
            return result.next();
        } catch (SQLException e) {
            return false;
        }
    }
}
