package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.model.enums.LogType;
import com.acervo.acervoespirita.model.enums.UserRole;
import com.acervo.acervoespirita.model.enums.UserStatus;
import com.acervo.acervoespirita.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LogService logService;

    // Cria um novo usuário
    @Transactional
    public User createUser(User user, User createdBy) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username já cadastrado.");
        }
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        boolean isAdmin = createdBy.isAdmin();
        boolean isStaffCreatingUser = createdBy.isStaff() && user.getRole() == UserRole.USER;

        if (!isAdmin && !isStaffCreatingUser) {
            throw new IllegalStateException(
                    "Usuário sem permissão para criar esse tipo de usuário."
            );
        }

        User savedUser = userRepository.save(user);

        logService.register(
                LogType.USER_CREATED,
                createdBy,
                "Usuário " + savedUser.getUsername() + " foi criado."
        );

        return savedUser;
    }

    //Atualiza usuario
    @Transactional
    public User updateUser(Long id, String name, String email, String phone, UserRole role, User updatedBy) {

        User user = findById(id);

        if (email != null
                && !email.equals(user.getEmail())
                && userRepository.existsByEmail(email)) {

            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);

        if (role != null
                && updatedBy.isAdmin()
                && role != user.getRole()) {

            UserRole oldRole = user.getRole();

            user.changeRole(role);

            logService.register(LogType.USER_ROLE_UPDATED,
                    updatedBy,
                    "Role do usuário "
                            + user.getUsername()
                            + " alterada de "
                            + oldRole
                            + " para "
                            + role
                            + ".");
        }

        User updatedUser = userRepository.save(user);

        logService.register(LogType.USER_UPDATED,
                updatedBy,
                "Usuário "
                        + updatedUser.getUsername()
                        + " foi atualizado.");

        return updatedUser;
    }


    // Inativa usuário
    @Transactional
    public User deactivateUser(Long id, User deactivatedBy) {

        User user = findById(id);
        user.deactivate();
        User updatedUser = userRepository.save(user);

        logService.register(
                LogType.USER_DEACTIVATED,
                deactivatedBy,
                "Usuário " + updatedUser.getUsername() + " foi inativado."
        );

        return updatedUser;
    }

    // Reativa usuário
    @Transactional
    public User reactivateUser(Long id, User reactivatedBy) {

        User user = findById(id);
        user.setStatus(UserStatus.ACTIVE);
        User updatedUser = userRepository.save(user);

        logService.register(
                LogType.USER_REACTIVATED,
                reactivatedBy,
                "Usuário " + updatedUser.getUsername() + " foi reativado."
        );

        return updatedUser;
    }

    // Busca usuário por id
    @Transactional(readOnly = true)
    public User findById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuário não encontrado.")
                );
    }

    // Busca usuário por email
    @Transactional(readOnly = true)
    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuário não encontrado.")
                );
    }

    // Busca usuário por username
    @Transactional(readOnly = true)
    public User findByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuário não encontrado.")
                );
    }

    // Busca usuários por nome
    @Transactional(readOnly = true)
    public List<User> findByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    // Lista usuários por status
    @Transactional(readOnly = true)
    public List<User> findByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }

    // Lista usuários por role
    @Transactional(readOnly = true)
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    // Lista todos os usuários
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
}