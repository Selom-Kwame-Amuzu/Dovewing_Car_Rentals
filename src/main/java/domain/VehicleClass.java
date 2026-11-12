package domain;

import java.math.BigDecimal;

public enum VehicleClass {
    ECONOMY,
    PREMIUM,
    VAN;

    public BigDecimal calculateCost(BigDecimal dailyRate, int days) {
        if (dailyRate == null || dailyRate.signum() <= 0) {
            throw new IllegalArgumentException("daily rate must be greater than zero");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("days must be greater than zero");
        }
        return dailyRate.multiply(BigDecimal.valueOf(days));
    }

    public BigDecimal calculateDeposit(BigDecimal deposit, RenterType renterType) {
        if (deposit == null || deposit.signum() < 0) {
            throw new IllegalArgumentException("deposit must not be negative");
        }
        if (renterType == null) {
            throw new IllegalArgumentException("renter type must not be null");
        }
        return renterType == RenterType.MEMBER ? BigDecimal.ZERO : deposit;
    }
}
