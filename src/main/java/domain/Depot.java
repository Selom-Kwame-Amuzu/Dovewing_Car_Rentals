package domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Depot {
    private final String name;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Renter> renters = new ArrayList<>();
    private final List<Rental> rentals = new ArrayList<>();

    public Depot(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public List<Renter> getRenters() {
        return Collections.unmodifiableList(renters);
    }

    public List<Rental> getRentals() {
        return Collections.unmodifiableList(rentals);
    }

    public void addVehicle(Vehicle v) {
        vehicles.add(v);
    }

    public void addRenter(Renter r) {
        renters.add(r);
    }

    public Rental rent(Renter r, Vehicle v, int days) {
        BigDecimal cost = v.getCategory().calculateCost(v.getDailyRate(), days);
        BigDecimal deposit = v.getCategory().calculateDeposit(v.getDeposit(), r.getKind());

        v.rentOut();
        r.addHistoryEntry("Rented " + v.getModel());
        Rental rental = new Rental(r, v, days, cost, deposit);
        rentals.add(rental);
        return rental;
    }

    public void returnVehicle(Vehicle v) {
        v.giveBack();
        for (Rental rt : rentals) {
            if (rt.getVehicle().equals(v) && rt.isActive()) {
                rt.markReturned();
            }
        }
    }
}
