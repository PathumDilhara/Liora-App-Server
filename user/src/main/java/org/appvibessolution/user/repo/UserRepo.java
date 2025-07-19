package org.appvibessolution.user.repo;

import org.appvibessolution.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<AppUser, String> {

    @Query(value = "SELECT * FROM users WHERE email=?1", nativeQuery = true)
    List<AppUser> searchUsers(String searchTerm);

    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
