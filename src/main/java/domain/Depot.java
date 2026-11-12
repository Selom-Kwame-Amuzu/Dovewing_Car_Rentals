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
        if (v == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }
        boolean duplicate = vehicles.stream()
                .anyMatch(existing -> existing.getReg().trim().equalsIgnoreCase(v.getReg().trim()));
        if (duplicate) {
            throw new IllegalArgumentException("vehicle with registration " + v.getReg() + " already exists");
        }
        vehicles.add(v);
    }

    public void addRenter(Renter r) {
        if (r == null) {
            throw new IllegalArgumentException("renter must not be null");
        }
        boolean duplicate = renters.stream()
                .anyMatch(existing -> existing.getName().trim().equalsIgnoreCase(r.getName().trim()));
        if (duplicate) {
            throw new IllegalArgumentException("renter with name " + r.getName() + " already exists");
        }
        renters.add(r);
    }

    public Rental rent(Renter r, Vehicle v, int days) {
        if (r == null) {
            throw new IllegalArgumentException("renter must not be null");
        }
        if (v == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("days must be greater than zero");
        }
        if (renters.stream().noneMatch(existing -> existing.getId() == r.getId())) {
            throw new IllegalArgumentException("renter is not registered with this depot");
        }
        if (vehicles.stream().noneMatch(existing -> existing.getId() == v.getId())) {
            throw new IllegalArgumentException("vehicle is not registered with this depot");
        }

        BigDecimal cost = v.getCategory().calculateCost(v.getDailyRate(), days);
        BigDecimal deposit = v.getCategory().calculateDeposit(v.getDeposit(), r.getKind());

        v.rentOut();
        r.addHistoryEntry("Rented " + v.getModel());
        Rental rental = new Rental(r, v, days, cost, deposit);
        rentals.add(rental);
        return rental;
    }

    public Rental returnVehicle(Vehicle v) {
        if (v == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }
        List<Rental> active = rentals.stream()
                .filter(rt -> rt.getVehicle().equals(v) && rt.isActive())
                .toList();
        if (active.isEmpty()) {
            throw new IllegalStateException(v.getModel() + " (" + v.getReg() + ") is not currently rented");
        }
        if (active.size() > 1) {
            throw new IllegalStateException("Multiple active rentals found for " + v.getReg());
        }

        Rental rental = active.get(0);
        v.giveBack();
        rental.markReturned();
        return rental;
    }
}
