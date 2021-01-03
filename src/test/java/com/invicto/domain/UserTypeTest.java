package com.invicto.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTypeTest {

    @Test
    public void testToString() {
        assertEquals(UserType.OWNER.toString(), "room_owner");
        assertEquals(UserType.GUEST.toString(), "room_guest");
    }
}
