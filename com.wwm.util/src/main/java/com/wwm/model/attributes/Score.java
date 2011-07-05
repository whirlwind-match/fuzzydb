package com.wwm.model.attributes;

import java.util.Collection;

public interface Score {
    float total();
    float forwardsTotal();
    float reverseTotal();

    float getForwardsScore(String name);
    float getReverseScore(String name);

    /**
     * Return available annotations
     */
    Collection<String> getScorerAttrNames();
}
