package org.l5g7.mealcraft.service;

import org.springframework.stereotype.Service;

@Service
public class AvatarServiceImpl implements AvatarService {
    @Override
    public String buildDefaultAvatar(String username) {
        return "https://avatarFor" + username;
    }
}
