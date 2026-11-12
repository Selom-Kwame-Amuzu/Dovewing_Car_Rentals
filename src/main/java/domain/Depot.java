package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public void rent(Renter r, Vehicle v, int days) {
        BigDecimal cost = BigDecimal.ZERO;
        BigDecimal deposit = BigDecimal.ZERO;

        if (v.getCategory() == VehicleClass.ECONOMY) {
            cost = v.getDailyRate().multiply(BigDecimal.valueOf(days));
            deposit = v.getDeposit();
            if (r.getKind() == RenterType.MEMBER) {
                deposit = BigDecimal.ZERO;
            }
            v.rentOut();
            System.out.println("Rented " + v.getModel() + " (" + v.getReg() + ") to " + r.getName()
                    + " for " + days + " days, cost GHS " + cost.setScale(2, RoundingMode.HALF_UP)
                    + ", deposit GHS " + deposit.setScale(2, RoundingMode.HALF_UP));
        } else if (v.getCategory() == VehicleClass.PREMIUM) {
            cost = v.getDailyRate().multiply(BigDecimal.valueOf(days));
            deposit = v.getDeposit();
            if (r.getKind() == RenterType.MEMBER) {
                deposit = BigDecimal.ZERO;
            }
            v.rentOut();
            System.out.println("Rented " + v.getModel() + " (" + v.getReg() + ") to " + r.getName()
                    + " for " + days + " days, cost GHS " + cost.setScale(2, RoundingMode.HALF_UP)
                    + ", deposit GHS " + deposit.setScale(2, RoundingMode.HALF_UP));
        }
        r.addHistoryEntry("Rented " + v.getModel());
        rentals.add(new Rental(r.getName(), v.getReg(), days, cost, deposit));
    }

    public void returnVehicle(Vehicle v) {
        v.giveBack();
        for (Rental rt : rentals) {
            if (rt.getVehicleReg().equals(v.getReg()) && rt.isActive()) {
                rt.markReturned();
            }
        }
        System.out.println("Returned " + v.getModel() + " (" + v.getReg() + ")");
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Fleet status:");
        for (Vehicle v : vehicles) {
            System.out.println("  " + v.getModel() + " (" + v.getReg() + ", " + v.getCategory() + "): " + v.getStatus());
        }
        System.out.println("Active rentals:");
        for (Rental rt : rentals) {
            if (rt.isActive()) {
                System.out.println("  Rental #" + rt.getId() + ": " + rt.getVehicleReg() + " -> " + rt.getRenterName()
                        + " (" + rt.getDays() + " days)");
            }
        }
    }
}
