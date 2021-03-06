package space.meduzza.property.service.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import space.meduzza.property.model.UserEntity;
import space.meduzza.property.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return new User(userEntity.getEmail(), userEntity.getPassword(), UserService.getAuthorities(userEntity));
    }

    @Override
    public UserEntity createUser(
            final String email,
            final String password
    ) {
        return userRepository.save(new UserEntity(email, passwordEncoder.encode(password), "ROLE_USER", List.of()));
    }

    @Override
    public UserEntity createUserIfNotExist(final String email) {
        return findUserByEmail(email).orElseGet(() -> createUser(email, randomPassword()));
    }

    @Override
    public Optional<UserEntity> findUserById(final long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    private String randomPassword() {
        return UUID
                .randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toLowerCase();
    }
}
