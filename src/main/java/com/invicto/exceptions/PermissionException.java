package com.invicto.exceptions;

import com.invicto.domain.User;

public class PermissionException extends IllegalAccessException {

	public PermissionException(String msg) {
		super(msg);
	}

	public static PermissionException notEnoughPermission(int userId) {
		return new PermissionException("Not enough permission for user: " + userId);
	}

	public static PermissionException notEnoughPermission(User user) {
		return new PermissionException("Not enough permission for user: " + user.getId());
	}
}
