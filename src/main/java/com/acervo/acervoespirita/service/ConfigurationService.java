package com.acervo.acervoespirita.service;

import com.acervo.acervoespirita.model.Configuration;
import com.acervo.acervoespirita.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    //Inicializa a configuração padrão do sistema
    @Transactional
    public void initializeDefaultConfiguration() {
        if (configurationRepository.existsById(1L)) {
            return;
        }
        configurationRepository.save(new Configuration());
    }

    //Retorna a configuração atual do sistema.
    @Transactional(readOnly = true)
    public Configuration getCurrentConfiguration() {
        return configurationRepository.findById(1L)
                .orElseThrow(() ->
                        new IllegalStateException("Configurações do Sistema não encontradas."));
    }

    //Atualiza a quantidade máxima de livros por empréstimo.
    @Transactional
    public Configuration updateMaxBooksPerLoan(Integer maxBooksPerLoan) {
        validateNonNegativeValue(maxBooksPerLoan, "Quantidade máxima de livros por Empréstimo");
        Configuration configuration = getCurrentConfiguration();
        configuration.setMaxBooksPerLoan(maxBooksPerLoan);
        return configurationRepository.save(configuration);
    }

    // Atualiza o limite de dias de empréstimo.
    @Transactional
    public Configuration updateLoanDaysLimit(Integer loanDaysLimit) {
        validateNonNegativeValue(loanDaysLimit, "Máximo de dias limites por empréstimo");
        Configuration configuration = getCurrentConfiguration();
        configuration.setLoanDaysLimit(loanDaysLimit);
        return configurationRepository.save(configuration);
    }

    //Atualiza a permissão de renovação
    @Transactional
    public Configuration updateAllowRenewal(Boolean allowRenewal) {
        Configuration configuration = getCurrentConfiguration();
        configuration.setAllowRenewal(allowRenewal);
        return configurationRepository.save(configuration);
    }

    //Valida valores negativos.
    private void validateNonNegativeValue(Integer value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " não pode ser vazio.");
        }
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " não pode ser negativo.");
        }
    }
}