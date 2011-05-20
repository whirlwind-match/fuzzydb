package whirlwind.demo.gui.formatters;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DayDateFormatter extends BaseFormatter {

	@Override
	public String format(Object attr) {
		Date date = (Date) attr;
		
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
	}
}
