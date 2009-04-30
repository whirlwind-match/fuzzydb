package com.wwm.model.attributes;

import java.util.Collection;

public interface Score {
    public float total();
    public float forwardsTotal();
    public float reverseTotal();

    /**
     * Set an attribute.  This is to be used for at least, Distance, but, in
     * something like PathDeviationScorer, could be used to record:
     * DriverDistance, TotalDetour
     */
    public void setScorerAttribute(String name, float value);

    public float getScorerAttributeAsFloat(String name);

    /**
     * Return available annotations
     */
    public Collection<String> getScorerAttrNames();
}
