package com.acervo.acervoespirita.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name="tb_configuration")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Configuration implements Serializable {

    @Id
    @Setter(AccessLevel.NONE)
    private Long id = 1L;

    private Integer maxBooksPerLoan;
    private Integer loanDaysLimit;
    private Boolean allowRenewal = true;

    public Configuration(Integer maxBooksPerLoan, Integer loanDaysLimit, Boolean allowRenewal) {
        this.id = 1L;
        this.maxBooksPerLoan = maxBooksPerLoan;
        this.loanDaysLimit = loanDaysLimit;
        this.allowRenewal = allowRenewal;
    }

}
