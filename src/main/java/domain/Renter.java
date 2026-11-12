package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Renter {
    private static final AtomicInteger nextId = new AtomicInteger(1);

    private final int id;
    private final String name;
    private final RenterType kind;
    private final List<String> history = new ArrayList<>();

    public Renter(String name, RenterType kind) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        if (kind == null) {
            throw new IllegalArgumentException("kind must not be null");
        }
        this.id = nextId.getAndIncrement();
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
