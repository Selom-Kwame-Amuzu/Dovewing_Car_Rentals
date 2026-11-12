package domain;

import java.math.BigDecimal;

public class Rental {
    public int id;
    public String renterName;
    public String vehicleReg;
    public int days;
    public BigDecimal cost;
    public BigDecimal deposit;
    public RentalStatus status; // ACTIVE or RETURNED
    static int counter = 1;

    public Rental(String renterName, String vehicleReg, int days, BigDecimal cost, BigDecimal deposit) {
        this.id = counter;
        counter++;
        this.renterName = renterName;
        this.vehicleReg = vehicleReg;
        this.days = days;
        this.cost = cost;
        this.deposit = deposit;
        this.status = RentalStatus.ACTIVE;
    }
}
