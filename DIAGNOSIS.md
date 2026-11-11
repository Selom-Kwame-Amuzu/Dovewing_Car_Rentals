# Diagnosis: Dovewing Rental Tracker 


## The most dangerous problem: A car can be rented while already rented.

**Symptom.** `Depot.rent()` never checks `vehicle.status`. In `main`, the Vitz goes to Kofi and then to Ama. Both calls succeed, the fleet view shows the Vitz as `RENTED`, and the active-rentals list shows two rentals for `GT-1234-20`. The same car is "available and rented at the same time" in the sense the owner describes: the status says one thing and the rental records say another.

**Root cause.** Availability is stored in two places that nothing keeps consistent: the vehicle's `status` string, and the list of `ACTIVE` rentals. `status` is a public, writable `String`, so any code can set it to anything, and `rent()` does not consult it before acting. The rule "a car is available or rented, never both" is not enforced anywhere.

**Why it is the worst one for this business.**
- The car is the business's asset. The one question the tool exists to answer is "who has this car right now?", and it gives two answers while looking healthy.
- Two customers are promised one car. Deposits may be taken twice, and refunds and compensation follow. The error is only discovered when someone turns up for a car that is not there.
- It corrupts later steps: `returnVehicle()` closes every active rental for that plate, so one return erases both records. If the car is then lost, damaged or kept too long, the business cannot say who had it.
- It is the reason the front desk keeps a paper backup, so the tool currently creates extra work.

Every other problem below is either contained or only matters once this one is fixed. This one undermines the purpose of the tool.

## All problems, ranked

Rank 1 is the most important for this business. Severity groups the ranks: **Critical** breaks the core promise, **Serious** gives silent wrong results, and **Maintainability** explains why changes keep breaking things.

| Rank | Problem | Severity | Root cause | Business risk |
|------|---------|----------|-----------|---------------|
| 1 | Double-booking (above) | Critical | Status and rentals are duplicated and unguarded; `rent()` has no availability check | Two customers, one car; no reliable record of who holds it |
| 2 | `returnVehicle()` closes **all** ACTIVE rentals for a plate, and "succeeds" even if the car was never out | Critical | Lookup by plate string only; no check of current state; a rental has no identity the caller can target | Wrong person's record closed; false "Returned" confirmations hide mistakes |
| 3 | An unknown `category` creates a rental with cost 0 and deposit 0, leaves the car AVAILABLE, and prints nothing | Serious | `if / else if` with no `else`; categories are free strings | Free rentals and lost revenue, with no error to notice. The commercial-van class would fall straight into this hole |
| 4 | No input validation: `days` of 0 or negative, null renter or vehicle, negative rates, renter or vehicle not registered with the depot | Serious | No guard clauses; objects are never checked against the depot's own lists | Zero or negative charges, crashes mid-transaction, rentals for cars the depot does not own |
| 5 | Duplicate plates and renters are accepted | Serious | `addVehicle` / `addRenter` do not check uniqueness | Plate lookups become ambiguous, which makes rank 2 worse |
| 6 | `"member"` or `"Premium"` behave differently from `"MEMBER"` / `"PREMIUM"` | Serious | Stringly-typed state compared with `equals` | A member silently loses the deposit waiver, or a car falls into rank 3 |
| 7 | Any caller can set `vehicle.status` directly, and `getRentals()` hands out the live list | Serious | Public mutable fields; weak encapsulation | Every rule can be bypassed from outside, so no rule can be trusted |
| 8 | The whole program is one file (`RentalTracker.java`): the demo `main`, five classes, and every business rule | Maintainability | No separation by responsibility; `Depot` acts as a "god class" that prices, validates, changes state, logs history and prints | A change anywhere risks everything else, which is how a premium pricing tweak once broke economy returns. Nothing can be reviewed or tested alone, and adding the van class means editing the same file that holds every other rule |
| 9| The ECONOMY and PREMIUM branches in `rent()` are identical, line for line | Maintainability | Copy-paste; the category does nothing | A pricing tweak must be made twice and will drift apart. This is how a premium change once broke economy handling. A van class would mean a third copy |
| 10 | `rent()` mixes pricing, deposit policy, state changes, history and printing, and reports success only by printing | Maintainability | No separation of responsibilities; no return value or exception | Rules cannot be tested alone, and a caller cannot tell whether a rental succeeded |
| 11 | `Rental` refers to `renterName` and `vehicleReg` strings | Maintainability | No real references; the generated `id` fields are never used | Two renters with the same name cannot be told apart; records break if a name or plate changes |
| 12 | Static ID counters and `double` for money | Maintainability | Global mutable state; floating point for currency | Tests cannot reset IDs; rounding drift once discounts or fees arrive |

## Planned structure (sketch)

### Directory tree

### Directory tree

### Directory tree

```
dovewing-rentals/
├── README.md                  design notes
├── DIAGNOSIS.md               diagnosis and planned structure
├── design-sketch.drawio       class diagram
├── expected-output.txt        original program's output (behaviour evidence)
└── src/
    ├── app/
    │   ├── RentalTracker.java         entry point; demo scenario, same as the original
    │   └── ReportPrinter.java         output kept apart from the rules
    └── domain/
        ├── Depot.java                 coordinates; no pricing rules
        ├── Vehicle.java               owns its status privately
        ├── VehicleStatus.java         enum: AVAILABLE, RENTED
        ├── VehicleClass.java          enum: ECONOMY, PREMIUM (VAN added here)
        ├── Renter.java
        ├── RenterType.java            enum: CASUAL, MEMBER (deposit waiver)
        ├── Rental.java                real references, not strings
        ├── RentalStatus.java          enum: ACTIVE, RETURNED
        └── VehicleUnavailableException.java
```

The `app` package holds the entry point and the output. The `domain` package holds
every business class and rule, and does not depend on `app`. A new vehicle class
means one new entry in `VehicleClass.java`, and nothing else changes.

The original is one file; this splits it so each rule has one home. A new
vehicle class means one new entry in `VehicleClass.java`, and nothing else
changes. File names may change as I build, and the design notes will say so.

How the design answers the diagnosis:
- **Ranks 1, 2, 7.** `Vehicle` owns its status privately. The only way to change it is `rentOut()` and `giveBack()`, which refuse an invalid move. A contradictory status cannot be expressed. The rental list is derived from, or checked against, that single source of truth.
- **Ranks 3, 6.** Category and renter type become enums, so a typo is a compile error and an unknown category cannot exist.
- **Rank 9 and the van.** Pricing and deposit rules live with the vehicle class, so adding vans means adding one entry, not editing `Depot.rent()`.
- **Ranks 10, 11.** `Depot` coordinates, `Rental` holds real references, and printing is kept separate from the rules.

The design may change while I build. If it does, the README design notes will say what changed and why.

## Summary

Most of these problems come from two root causes: **state that is duplicated and unguarded** (status vs. rentals, free strings, public fields) and **no checks at the point of action** (`rent` and `return` accept anything). Fixing the double-booking rule first protects the business's most valuable asset and is the reason the staff distrust the tool.
