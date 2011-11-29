package com.wwm.model.attributes;

import java.util.Collection;

public interface Score {
    float total();
    float forwardsTotal();
    float reverseTotal();

    /**
     * Return the individual forwards score for the named scorer
     * @param name name of the scorer
     * @return the score, or null if no score was recorded for this direction
     */
    Float getForwardsScore(String name);

    /**
     * Return the individual reverse score for the named scorer
     * @param name name of the scorer
     * @return the score, or null if no score was recorded for this direction
     */
    Float getReverseScore(String name);

    /**
     * Returns the names for the available match results
     * <br>TODO: Rename - shouldn't refer to Attr as matcher can be anything
     */
    Collection<String> getScorerAttrNames();
}
