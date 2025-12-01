package org.l5g7.mealcraft.app.user;

public interface CurrentUserProvider {
    User getCurrentUserOrNullIfAdmin();
}
