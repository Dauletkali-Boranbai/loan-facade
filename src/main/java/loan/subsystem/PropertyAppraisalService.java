package loan.subsystem;

import loan.model.PropertyInfo;

import java.util.Map;

public class PropertyAppraisalService {

    private static final double DEFAULT_CITY_FACTOR = 0.90;

    private final Map<String, Double> cityFactors = Map.of(
            "Astana", 1.00,
            "Almaty", 1.05,
            "Shymkent", 0.92);

    public long appraise(PropertyInfo property) {
        String city = property.address().split(",")[0].trim();
        double factor = cityFactors.getOrDefault(city, DEFAULT_CITY_FACTOR);
        return Math.round(property.declaredValue() * factor);
    }
}
