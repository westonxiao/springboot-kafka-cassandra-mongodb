package com.healthcare.account.repo;

import com.healthcare.account.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query example (optional)
    User findByUsername(String username);
}