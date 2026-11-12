package domain;

import java.math.BigDecimal;

public class Vehicle {
    private static int nextId = 1;

    private final int id;
    private final String model;
    private final String reg;
    private final BigDecimal dailyRate;
    private final BigDecimal deposit;
    private final VehicleClass category;
    private VehicleStatus status;

    public Vehicle(String model, String reg, BigDecimal dailyRate, BigDecimal deposit, VehicleClass category) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("model must not be null or blank");
        }
        if (reg == null || reg.isBlank()) {
            throw new IllegalArgumentException("registration must not be null or blank");
        }
        if (dailyRate == null || dailyRate.signum() <= 0) {
            throw new IllegalArgumentException("daily rate must be greater than zero");
        }
        if (deposit == null || deposit.signum() < 0) {
            throw new IllegalArgumentException("deposit must not be negative");
        }
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }
        this.id = nextId++;
        this.model = model;
        this.reg = reg;
        this.dailyRate = dailyRate;
        this.deposit = deposit;
        this.category = category;
        this.status = VehicleStatus.AVAILABLE;
    }

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getReg() {
        return reg;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public VehicleClass getCategory() {
        return category;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    public void rentOut() {
        if (status != VehicleStatus.AVAILABLE) {
            throw new VehicleUnavailableException(model + " (" + reg + ") is not available for rent");
        }
        this.status = VehicleStatus.RENTED;
    }

    public void giveBack() {
        this.status = VehicleStatus.AVAILABLE;
    }
}
