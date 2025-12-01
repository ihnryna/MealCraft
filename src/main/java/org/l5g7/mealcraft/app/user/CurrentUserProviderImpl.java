package org.l5g7.mealcraft.app.user;

import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProviderImpl implements CurrentUserProvider {

    private final UserRepository userRepository;

    public CurrentUserProviderImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUserOrNullIfAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return null;
        }

        String name = authentication.getName();
        return userRepository.findByUsername(name)
                .orElseThrow(() -> new EntityDoesNotExistException("User", "name", name));
    }
}
