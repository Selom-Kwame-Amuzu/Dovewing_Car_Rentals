package app;

import domain.Depot;
import domain.Renter;
import domain.Vehicle;

public class RentalTracker {

    public static void main(String[] args) {
        Depot d = new Depot("Dovewing Car Rentals");

        Vehicle vitz = new Vehicle("Toyota Vitz", "GT-1234-20", 150.00, 500.00, "ECONOMY");
        Vehicle prado = new Vehicle("Toyota Prado", "GR-5678-22", 600.00, 2000.00, "PREMIUM");
        d.addVehicle(vitz);
        d.addVehicle(prado);

        Renter kofi = new Renter("Kofi Mensah", "CASUAL");
        Renter ama = new Renter("Ama Owusu", "MEMBER");
        d.addRenter(kofi);
        d.addRenter(ama);

        d.rent(kofi, vitz, 3);
        d.rent(ama, prado, 2);
        d.returnVehicle(prado);

        d.rent(ama, vitz, 2);

        d.printReport();
    }
}
