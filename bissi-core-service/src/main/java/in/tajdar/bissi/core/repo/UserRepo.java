package in.tajdar.bissi.core.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.tajdar.bissi.core.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
