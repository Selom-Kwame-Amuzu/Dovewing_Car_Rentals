package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renter {
    private static int nextId = 1;

    private final int id;
    private final String name;
    private final RenterType kind;
    private final List<String> history = new ArrayList<>();

    public Renter(String name, RenterType kind) {
        this.id = nextId++;
        this.name = name;
        this.kind = kind;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RenterType getKind() {
        return kind;
    }

    public List<String> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void addHistoryEntry(String entry) {
        history.add(entry);
    }
}
