package org.appvibessolution.user.repo;

import org.appvibessolution.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepo extends JpaRepository<User, String> {

    @Query(value = "SELECT * FROM users WHERE email=?1", nativeQuery = true)
    List<User> searchUsers(String searchTerm);
}
