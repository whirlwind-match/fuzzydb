package org.fuzzydb.util.web;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class TagLibFunctionsTest {
    
    @Test
    public void testDateAsMinsHoursDaysEtcAgo() {
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -2);
        Date now = cal.getTime();

        cal.add(Calendar.MINUTE, -1);
        Date oneMinsAgo = cal.getTime();

        cal.add(Calendar.MINUTE, -1);
        Date twoMinsAgo = cal.getTime();

        cal.add(Calendar.MINUTE, -57);
        Date fiveNineMinsAgo = cal.getTime();

        cal.add(Calendar.HOUR, -1);
        Date hourAndfiveNineMinsAgo = cal.getTime();

        cal.add(Calendar.HOUR, -1);
        Date twoHoursAndfiveNineMinsAgo = cal.getTime();

        cal.add(Calendar.HOUR, -21);
        Date twoThreeHoursAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, -1);
        Date dayAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, -1);
        Date twoDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, -4);
        Date sixDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, -1);
        Date sevenDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, -7);
        Date fourteenDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, -15);
        Date twoNineDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, -1);
        Date thirtyDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, -30);
        Date sixtyDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, -299);
        Date twoFiveNineDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, -1);
        Date threeSixtyDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, -5);
        Date threeSixFiveDaysAgo = cal.getTime();

        assertEquals("< 2 mins ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(now));
        assertEquals("< 2 mins ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(oneMinsAgo));
        assertEquals("2 mins ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(twoMinsAgo));
        assertEquals("59 mins ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(fiveNineMinsAgo));
        assertEquals("1 hour ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(hourAndfiveNineMinsAgo));
        assertEquals("2 hours ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(twoHoursAndfiveNineMinsAgo));
        assertEquals("23 hours ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(twoThreeHoursAgo));
        assertEquals("47 hours ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(dayAgo));
        assertEquals("2 days ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(twoDaysAgo));
        assertEquals("6 days ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(sixDaysAgo));
        assertEquals("a week ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(sevenDaysAgo));
        assertEquals("2 weeks ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(fourteenDaysAgo));
        assertEquals("4 weeks ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(twoNineDaysAgo));
        assertEquals("a month ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(thirtyDaysAgo));
        assertEquals("2 months ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(sixtyDaysAgo));
        assertEquals("11 months ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(twoFiveNineDaysAgo));
        assertEquals("12 months ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(threeSixtyDaysAgo));
        assertEquals("> 1 year ago", TagLibFunctions.dateAsMinsHoursDaysEtcAgo(threeSixFiveDaysAgo));

    }

}
