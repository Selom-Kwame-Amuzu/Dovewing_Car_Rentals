package domain;

import java.math.BigDecimal;

public class Vehicle {
    public String model;
    public String reg;
    public BigDecimal dailyRate;
    public BigDecimal deposit;
    public VehicleClass category; // ECONOMY or PREMIUM
    public VehicleStatus status;  // AVAILABLE or RENTED
    public int id;
    static int counter = 1;

    public Vehicle(String model, String reg, BigDecimal dailyRate, BigDecimal deposit, VehicleClass category) {
        this.id = counter;
        counter++;
        this.model = model;
        this.reg = reg;
        this.dailyRate = dailyRate;
        this.deposit = deposit;
        this.category = category;
        this.status = VehicleStatus.AVAILABLE;
    }
}
