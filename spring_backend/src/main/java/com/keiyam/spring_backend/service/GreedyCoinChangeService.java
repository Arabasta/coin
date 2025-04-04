package com.keiyam.spring_backend.service;

import com.keiyam.spring_backend.dto.CoinChangeRequest;
import com.keiyam.spring_backend.exception.InvalidCoinChangeRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Deque;
import java.util.List;

/**
 * Service for calculating the minimum number of coins needed to make up a given amount.
 */
@Slf4j
@Service
public class GreedyCoinChangeService extends AbstractCoinChangeService {

    /**
     * Calculates the minimum number of coins needed to make up the given amount using the provided denominations.
     *
     * @param request the request containing the total amount and denominations
     * @return a list of coins that make up the amount in ascending order
     * @throws InvalidCoinChangeRequestException if the exact amount cannot be made with the given denominations
     */
    @Override
    @Cacheable(value = "coinChangeResults", key = "#request.amount.toString() + '-' + #request.denominations.hashCode()")
    public Deque<BigDecimal> calculateMinCoinChange(CoinChangeRequest request) {
        log.info("Calculating minimum coins for amount: {}", request.getAmount());
        BigDecimal amount = request.getAmount();
        List<BigDecimal> denominations = request.getDenominations();

        sortDenominationsIfNeeded(denominations);

        Deque<BigDecimal> result = initDequeWithMinCapacity(denominations, amount);

        BigDecimal remainingAmount = calculateCoins(amount, denominations, result);
        if (remainingAmount.compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidCoinChangeRequestException("Cannot make the exact amount with the given denominations.");
        }

        log.info("Calculated minimum coins: {}", result);
        return result;
    }

    /**
     * Calculates the minimum number of coins needed and adds them to the result list.
     *
     * @param denominations the list of available denominations
     * @param result        the list to store the resulting coins
     */
    private BigDecimal calculateCoins(BigDecimal amount, List<BigDecimal> denominations, Deque<BigDecimal> result) {
        for (int i = denominations.size() - 1; i >= 0; i--) {
            BigDecimal currentDenomination = denominations.get(i);

            // mod amount by current denomination
            int numCoins = amount.divide(currentDenomination, RoundingMode.DOWN).intValue();
            amount = amount.remainder(currentDenomination);

            addCoinsToResult(currentDenomination, numCoins, result);

            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }
        return amount;
    }

}
