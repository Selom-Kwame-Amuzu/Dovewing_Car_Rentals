package domain;

import java.math.BigDecimal;

public class Rental {
    private static int nextId = 1;

    private final int id;
    private final String renterName;
    private final String vehicleReg;
    private final int days;
    private final BigDecimal cost;
    private final BigDecimal deposit;
    private RentalStatus status;

    public Rental(String renterName, String vehicleReg, int days, BigDecimal cost, BigDecimal deposit) {
        this.id = nextId++;
        this.renterName = renterName;
        this.vehicleReg = vehicleReg;
        this.days = days;
        this.cost = cost;
        this.deposit = deposit;
        this.status = RentalStatus.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public String getRenterName() {
        return renterName;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public int getDays() {
        return days;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == RentalStatus.ACTIVE;
    }

    public void markReturned() {
        this.status = RentalStatus.RETURNED;
    }
}
