package com.keiyam.spring_backend.config;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Configuration
@ConfigurationProperties(prefix = "valid-denominations")
public class DenominationConfig {
    private static final Logger logger = LoggerFactory.getLogger(DenominationConfig.class);

    private Set<BigDecimal> denominations;

    @CacheEvict(value = "coinChangeResults", allEntries = true)
    public void setDenominations(Set<BigDecimal> denominations) {
        this.denominations = denominations;
        logger.info("Loaded denominations: {}", denominations);
    }
}