package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private HashMap<Long, TimeEntry> store;
    private long nextTimeEntryId;

    public InMemoryTimeEntryRepository() {
        this.store = new HashMap<>();
        this.nextTimeEntryId = 1L;
    }

    public TimeEntry create(TimeEntry timeEntry) {
        TimeEntry timeEntry1 = new TimeEntry(this.nextTimeEntryId, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());

        this.store.put(this.nextTimeEntryId, timeEntry1);
        this.nextTimeEntryId++;
        return timeEntry1;
    }

    public TimeEntry find(long timeEntryId) {
        return this.store.get(timeEntryId);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(store.values());
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        if(this.find(id)==null) return null;

        TimeEntry timeEntry1 = new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        this.store.put(id, timeEntry1);
        return timeEntry1;
    }

    public void delete(long id) {
        this.store.remove(id);
    }
}
