package loan.subsystem;

import java.util.ArrayList;
import java.util.List;

public class ApplicationArchive {

    private final List<String> records = new ArrayList<>();

    public String save(String summary) {
        records.add(summary);
        return String.format("LN-%05d", records.size());
    }

    public int size() {
        return records.size();
    }
}
