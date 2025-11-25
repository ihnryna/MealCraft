package org.l5g7.mealcraft.app.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String email);
    long countByCreatedAtBetween(Date from, Date to);
}
