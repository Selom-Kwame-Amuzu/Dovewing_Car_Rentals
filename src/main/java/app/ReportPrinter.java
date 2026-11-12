package app;

import java.math.BigDecimal;
import java.math.RoundingMode;

import domain.Depot;
import domain.Rental;
import domain.Vehicle;
import domain.VehicleUnavailableException;

public class ReportPrinter {

    public void printRental(Rental rental) {
        System.out.println("Rented " + rental.getVehicle().getModel() + " (" + rental.getVehicle().getReg() + ") to "
                + rental.getRenter().getName() + " for " + rental.getDays() + " days, cost GHS "
                + format(rental.getCost()) + ", deposit GHS " + format(rental.getDeposit()));
    }

    public void printReturn(Vehicle vehicle) {
        System.out.println("Returned " + vehicle.getModel() + " (" + vehicle.getReg() + ")");
    }

    public void printUnavailable(VehicleUnavailableException exception) {
        System.out.println("Cannot rent: " + exception.getMessage());
    }

    public void printReport(Depot depot) {
        System.out.println();
        System.out.println("=== " + depot.getName() + " - end of day ===");
        System.out.println("Fleet status:");
        for (Vehicle v : depot.getVehicles()) {
            System.out.println("  " + v.getModel() + " (" + v.getReg() + ", " + v.getCategory() + "): " + v.getStatus());
        }
        System.out.println("Active rentals:");
        for (Rental rt : depot.getRentals()) {
            if (rt.isActive()) {
                System.out.println("  Rental #" + rt.getId() + ": " + rt.getVehicle().getReg() + " -> "
                        + rt.getRenter().getName() + " (" + rt.getDays() + " days)");
            }
        }
        System.out.println("End-of-day summary:");
        System.out.println("  Total revenue: GHS " + format(depot.getTotalRevenue()));
        System.out.println("  Outstanding deposits: GHS " + format(depot.getOutstandingDeposits()));
        System.out.println("  Active rentals: " + depot.getActiveRentalCount());
        System.out.println("  Available vehicles: " + depot.getAvailableVehicleCount());
    }

    private String format(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
