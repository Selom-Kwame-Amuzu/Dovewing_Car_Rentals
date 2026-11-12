package domain;

import java.math.BigDecimal;

public enum VehicleClass {
    ECONOMY,
    PREMIUM,
    VAN;

    public BigDecimal calculateCost(BigDecimal dailyRate, int days) {
        return dailyRate.multiply(BigDecimal.valueOf(days));
    }

    public BigDecimal calculateDeposit(BigDecimal deposit, RenterType renterType) {
        return renterType == RenterType.MEMBER ? BigDecimal.ZERO : deposit;
    }
}
