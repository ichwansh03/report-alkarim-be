package org.ichwan.dto.request;

import org.ichwan.util.UserRole;

public record AuthRequest(String name, Long classRoomId, String gender, UserRole roles, String regnumber, String password) {
}
