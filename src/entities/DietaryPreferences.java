package entities;

import java.util.Map;
import java.util.Set;

public interface DietaryPreferences {
    // float value of 1 is true, 0 is false, any other value for macros is considered as quantity per serving
    DietaryPreferences getDietaryRestrictions();

    void setDietaryRestrictions(Map<String, Float> dietaryRestrictions);

    void addRestriction(String restriction, Float res);

    Boolean removeRestriction(String restriction, Float res);

    Float getRestriction(String key);

    Set<String> getAllKeys();
}
