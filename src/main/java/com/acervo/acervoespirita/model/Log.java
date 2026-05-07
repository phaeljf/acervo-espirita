package com.acervo.acervoespirita.model;

import com.acervo.acervoespirita.model.enums.LogType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "logs")
@Setter
@Getter
@ToString(exclude = {"user"})
@NoArgsConstructor
public class Log implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant dateTime;

    @Column(nullable = false)
    private String description;

    @Builder
    public Log(LogType type, User user, String description) {
        this.type = type;
        this.user = user;
        this.dateTime = Instant.now();
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return Objects.equals(id, log.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
