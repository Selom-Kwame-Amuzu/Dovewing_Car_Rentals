package domain;

import java.math.BigDecimal;

public class Rental {
    private static int nextId = 1;

    private final int id;
    private final Renter renter;
    private final Vehicle vehicle;
    private final int days;
    private final BigDecimal cost;
    private final BigDecimal deposit;
    private RentalStatus status;

    public Rental(Renter renter, Vehicle vehicle, int days, BigDecimal cost, BigDecimal deposit) {
        if (renter == null) {
            throw new IllegalArgumentException("renter must not be null");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("days must be greater than zero");
        }
        if (cost == null || cost.signum() < 0) {
            throw new IllegalArgumentException("cost must not be negative");
        }
        if (deposit == null || deposit.signum() < 0) {
            throw new IllegalArgumentException("deposit must not be negative");
        }
        this.id = nextId++;
        this.renter = renter;
        this.vehicle = vehicle;
        this.days = days;
        this.cost = cost;
        this.deposit = deposit;
        this.status = RentalStatus.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public Renter getRenter() {
        return renter;
    }

    public Vehicle getVehicle() {
        return vehicle;
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
