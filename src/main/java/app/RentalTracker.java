package app;

import java.math.BigDecimal;

import domain.Depot;
import domain.Renter;
import domain.RenterType;
import domain.Vehicle;
import domain.VehicleClass;
import domain.VehicleUnavailableException;

public class RentalTracker {

    public static void main(String[] args) {
        Depot d = new Depot("Dovewing Car Rentals");
        ReportPrinter printer = new ReportPrinter();

        Vehicle vitz = new Vehicle("Toyota Vitz", "GT-1234-20", new BigDecimal("150.00"), new BigDecimal("500.00"), VehicleClass.ECONOMY);
        Vehicle prado = new Vehicle("Toyota Prado", "GR-5678-22", new BigDecimal("600.00"), new BigDecimal("2000.00"), VehicleClass.PREMIUM);
        Vehicle hiace = new Vehicle("Toyota Hiace", "GX-9012-23", new BigDecimal("800.00"), new BigDecimal("3000.00"), VehicleClass.VAN);
        d.addVehicle(vitz);
        d.addVehicle(prado);
        d.addVehicle(hiace);

        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        d.addRenter(kofi);
        d.addRenter(ama);

        printer.printRental(d.rent(kofi, vitz, 3));
        printer.printRental(d.rent(ama, prado, 2));
        d.returnVehicle(prado);
        printer.printReturn(prado);

        try {
            printer.printRental(d.rent(ama, vitz, 2));
        } catch (VehicleUnavailableException e) {
            printer.printUnavailable(e);
        }

        printer.printReport(d);
    }
}
