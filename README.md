# Dovewing Car Rentals — Fleet and Rental Tracker

A small Java 21 program the Dovewing depot uses to track its fleet, its renters,
and the rentals between them. It is plain Java with no external dependencies.

**Lab context:** this repository began as a deliberately flawed starter. The
problems found in it, ranked by business risk, are recorded in
[`DIAGNOSIS.md`](DIAGNOSIS.md); this README describes the rebuilt design and why
it looks the way it does.

## Running it

Compile every source file, then run the demo entry point:

```bash
javac -d target/classes $(find src/main/java -name "*.java")
java -cp target/classes app.RentalTracker
```

On Windows, use PowerShell, which expands the source list for you:

```powershell
javac -d target\classes (Get-ChildItem -Recurse src\main\java -Filter *.java | ForEach-Object FullName)
java -cp target\classes app.RentalTracker
```

If Maven is installed, `mvn compile` produces the same classes in
`target/classes`, and the run command is unchanged:

```bash
mvn compile
java -cp target/classes app.RentalTracker
```

There is no `exec` plugin configured, so `mvn exec:java` will not work.

## Project layout

```
src/main/java/
├── app/
│   ├── RentalTracker.java          demo entry point; builds the scenario
│   └── ReportPrinter.java          all console output
└── domain/
    ├── Depot.java                  coordinates fleet, renters, and rentals
    ├── Vehicle.java                owns its status and transition rules
    ├── Renter.java                 renter identity and rental history
    ├── Rental.java                 one rental, holding real object references
    ├── VehicleClass.java           ECONOMY, PREMIUM, VAN; pricing policy
    ├── VehicleStatus.java          AVAILABLE, RENTED
    ├── RenterType.java             CASUAL, MEMBER
    ├── RentalStatus.java           ACTIVE, RETURNED
    └── VehicleUnavailableException.java
```

`app` depends on `domain`; `domain` knows nothing about `app`. Every business
rule has exactly one home, so a change to pricing, availability, or output does
not ripple through unrelated classes.

## Design notes and why

### Status is owned by `Vehicle`

`Vehicle.status` is private and can only change through `rentOut()` and
`giveBack()`. `rentOut()` refuses anything that is not `AVAILABLE`;
`giveBack()` refuses anything that is not `RENTED`. A contradictory state — a
car that is somehow both available and rented — cannot be expressed from
outside the class.

This is the direct answer to the double-booking bug: the rule is enforced at
the only place the state can change, rather than being re-checked (or forgotten)
by callers.

### Availability is checked before anything changes

`Depot.rent()` asks the vehicle to `rentOut()` and lets
`VehicleUnavailableException` propagate. Because the check happens before the
rental is recorded, a rejected booking leaves the depot exactly as it was.

### A return closes exactly one rental

`returnVehicle(Vehicle)` finds the single active rental for that vehicle,
marks it returned, and returns it. Zero active rentals is an error, and so is
more than one. The caller never has to search by plate, and one return can no
longer erase several rental records at once.

### Enums instead of free strings

Vehicle class, vehicle status, renter type, and rental status are enums. A typo
becomes a compile error, and an unknown category can no longer silently produce
a free rental. Adding the commercial van class was one entry in
`VehicleClass`; nothing else needed to branch on it.

### Pricing policy lives with the vehicle class

`VehicleClass` owns `calculateCost` and `calculateDeposit`. Today the cost is
the daily rate times the number of days for every class, and the deposit is
waived for `MEMBER` renters. When a class needs different rules, the override
lives beside the enum constant instead of being duplicated inside
`Depot.rent()`.

### `Rental` holds real references

A rental points at a `Renter` and a `Vehicle`, not at copied name and plate
strings. Records therefore stay attached to the actual objects, two renters with
the same name remain distinguishable by id, and a changed plate cannot strand a
rental.

### Validation is split by responsibility

- Constructors enforce object invariants with `IllegalArgumentException`:
  non-blank names and registrations, positive rates, non-negative deposits,
  positive durations, non-null enums.
- `Depot` enforces depot-level rules: duplicate registrations and renter names,
  and whether the renter and vehicle are actually registered with this depot.

Uniqueness uses business keys (registration for vehicles, name for renters);
ids are used to check membership. A valid object that was never added to the
depot cannot be rented, which prevents orphan rentals that the fleet report
could never show.

### Money and ids

All money is `BigDecimal`, so totals do not accumulate floating-point drift.
Ids come from per-class `AtomicInteger` sequences, so concurrent creation cannot
hand out the same id, and numbering still starts at 1 on each run.

## Behaviour

The demo rents a Vitz to Kofi, rents a Prado to Ama, returns the Prado, then
tries to rent the Vitz to Ama while Kofi still has it. That last attempt is now
**rejected**:

```
Rented Toyota Vitz (GT-1234-20) to Kofi Mensah for 3 days, cost GHS 450.00, deposit GHS 500.00
Rented Toyota Prado (GR-5678-22) to Ama Owusu for 2 days, cost GHS 1200.00, deposit GHS 0.00
Returned Toyota Prado (GR-5678-22)
Cannot rent: Toyota Vitz (GT-1234-20) is not available for rent
```

The end-of-day report then lists all three vehicles, including the `VAN`, the
one rental that is still active, and a short summary of total revenue,
outstanding deposits, and fleet availability.

Rejecting the second Vitz booking is the only intentional behaviour change from
the original program. The original reported success for both bookings, which is
the double-booking defect described first in the diagnosis.

