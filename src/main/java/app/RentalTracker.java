package app;

import java.math.BigDecimal;

import domain.Depot;
import domain.Renter;
import domain.RenterType;
import domain.Vehicle;
import domain.VehicleClass;

public class RentalTracker {

    public static void main(String[] args) {
        Depot d = new Depot("Dovewing Car Rentals");

        Vehicle vitz = new Vehicle("Toyota Vitz", "GT-1234-20", new BigDecimal("150.00"), new BigDecimal("500.00"), VehicleClass.ECONOMY);
        Vehicle prado = new Vehicle("Toyota Prado", "GR-5678-22", new BigDecimal("600.00"), new BigDecimal("2000.00"), VehicleClass.PREMIUM);
        d.addVehicle(vitz);
        d.addVehicle(prado);

        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        d.addRenter(kofi);
        d.addRenter(ama);

        d.rent(kofi, vitz, 3);
        d.rent(ama, prado, 2);
        d.returnVehicle(prado);

        d.rent(ama, vitz, 2);

        d.printReport();
    }
}
