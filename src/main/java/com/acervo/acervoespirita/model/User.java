package com.acervo.acervoespirita.model;

import com.acervo.acervoespirita.model.enums.UserRole;
import com.acervo.acervoespirita.model.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"loans"})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    @Setter(AccessLevel.NONE)
    private String username;

    // Tanto o email como phone podem estar vazio sao dados so para contatos
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
    private UserRole role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE) //Nao permite setar uma nova lista de emprestimos, vai precisar add novos emprestimos na lista ja existente
    private List<Loan> loans = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Builder
    public User(String name, String email, String username, String phone, UserRole role) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("É necessário informar o nome!");
        }

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("É necessário informar o Usuário!");
        }

        if (role == null) {
            throw new IllegalArgumentException("É necessário informar o função do usuário!");
        }

        this.name = name;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    //Métodos criados

    //Metodos sobre o Usuario
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public void deactivate() {
        if (hasActiveLoan()) {
            throw new IllegalStateException("Erro ao Inativar: Usuário tem um empréstimo ativo!");
        }
        this.status = UserStatus.INACTIVE;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isStaff() {
        return role == UserRole.STAFF;
    }

    public void changeRole(UserRole newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("Role inválida");
        }
        this.role = newRole;
    }

    @PreUpdate
    public void updateTime() {
        updatedAt = Instant.now();
    }

    public boolean canHandleLoan() {
        return role == UserRole.STAFF || role == UserRole.ADMIN;
    }

    //Metodos sobre Emrpestimos

    public boolean canBorrow() {
        return isActive() && !hasActiveLoan();
    }

    public void addLoan(Loan loan) {
        loans.add(loan);
        loan.setUser(this);
    }

    public List<Loan> getLoans() {
        return Collections.unmodifiableList(loans);
    }

    public boolean hasActiveLoan() {
        return loans.stream().anyMatch(Loan::isActive);
    }

    public Loan getActiveLoan() {
        return loans.stream()
                .filter(Loan::isActive)
                .findFirst()
                .orElse(null);
    }


}

