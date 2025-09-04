package com.epm.gestepm.modelapi.common.utils;

import com.epm.gestepm.modelapi.common.config.ApplicationContextProvider;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.deprecated.user.exception.InvalidUserSessionException;
import com.epm.gestepm.modelapi.deprecated.user.service.UserServiceOld;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class Utiles {

	private static final Log log = LogFactory.getLog(Utiles.class);
	
	/**
	 * Encript String to MD5 String.
	 * @param text
	 * @return
	 */
	public static String textToMD5(String text) {
		
		try {
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte[] digest = md.digest();
			return DatatypeConverter.printHexBinary(digest).toUpperCase();
			
		} catch (NoSuchAlgorithmException e) {
			log.error(e);
			return null;
		}
	}
	
	/**
	 * Get Actual DateTime formated like dd/MM/yyyy HH:mm.
	 * @return
	 */
	public static String getActualDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return dateFormat.format(new Date());
	}
	
	public static Date transformSimpleStringToDate(String dateInString) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			log.error(e);
			return null;
		}
	}

	public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		
		if (dateToConvert == null) {
			return null;
		}
		
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static String transform(final LocalDateTime offsetDateTime, final String format) {
		return offsetDateTime.format(DateTimeFormatter.ofPattern(format));
	}

	public static LocalDateTime transform(final String dateTime, final String format) {
		return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(format));
	}

	public static String getDateFormatted(final LocalDateTime offsetDateTime) {
		return offsetDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	}

	public static String getDateFormatted(final LocalDateTime offsetDateTime, final String pattern) {
		return offsetDateTime != null ? offsetDateTime.format(DateTimeFormatter.ofPattern(pattern)) : null;
	}

	/**
	 * Get Date as String from Date ESP
	 * @param date
	 * @return
	 */
	public static String getDateFormattedESP(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
		return dateFormat.format(date);  
	}
	
	/**
	 * Get Date as String from Date
	 * @param date
	 * @return
	 */
	public static String getDateFormatted(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");  
		return dateFormat.format(date);  
	}

	public static String getDateAsText(int month, int year, Locale locale, MessageSource messageSource) {
		return messageSource.getMessage("month." + month, null, locale) + "-" + year;
	}

	public static User getUsuario() throws InvalidUserSessionException {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();

		if (user == null) {
			throw new InvalidUserSessionException();
		}
		
		// Load Service
		UserServiceOld userServiceOld = ApplicationContextProvider.getBean(UserServiceOld.class);
		
		// Reload from DB
		return userServiceOld.getUserById(user.getId());
	}

	public static User getCurrentUser() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (User) authentication.getDetails();
	}
	
	public static byte[] base64PngToByteArray(String base64Img) {
		String base64 = base64Img.replace("data:image/png;base64,", "");
		return base64ToByteArray(base64);
	}
	
	/**
	 * String base64 to byte[]
	 * @param base64
	 * @return
	 */
	public static byte[] base64ToByteArray(String base64) {
		return Base64.getDecoder().decode(base64);
	}

	public static String secondsToHoursAndMinutesAndSecondsString(int t) {

		int hours = t / 3600;
		int secondsLeft = t - hours * 3600;
		int minutes = secondsLeft / 60;
		int seconds = secondsLeft - minutes * 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	public static int getDaysOfMonth(int month, int year) {
		YearMonth yearMonthObject = YearMonth.of(year, month);
		return yearMonthObject.lengthOfMonth();
	}
	
	public static boolean isWeekend(final LocalDate localDate) {
		final DayOfWeek day = localDate.getDayOfWeek();
		return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
	}

	public static boolean isSameDay(final LocalDateTime dt1, final LocalDateTime dt2) {
		return dt1.toLocalDate().equals(dt2.toLocalDate());
	}

	public static String formatDurationHHMM(int seconds) {
		if (seconds == 0) {
			return null;
		}

		final boolean isNegative = seconds < 0;
		seconds = Math.abs(seconds);

		final long hours = TimeUnit.SECONDS.toHours(seconds);
		final long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;

		final String formatted = String.format("%02d:%02d", hours, minutes);
		return isNegative ? "-" + formatted : formatted;
	}

	public static long getHours(LocalDateTime lc1, LocalDateTime lc2) {
		return getTemporal(lc1, lc2, ChronoUnit.HOURS);
	}

	public static long getTemporal(LocalDateTime lc1, LocalDateTime lc2, TemporalUnit temporal) {
		if (lc1 == null || lc2 == null || temporal == null)
			return 0;

		return temporal.between(lc1, lc2);
	}

	public static double getHoursWithMinutesPart(LocalDateTime lc1, LocalDateTime lc2) {
		Duration duration = Duration.between(lc1, lc2);

		return duration.toHours() + duration.toMinutesPart() / 60.0;
	}

	public static final int SECONDS_PER_MINUTE = 60;
	public static final int MINUTES_PER_HOUR = 60;
	public static final int HOURS_PER_DAY = 24;
	public static final int SECONDS_PER_DAY = (HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE);

	private static final Pattern TIME_SEPARATOR_PATTERN = Pattern.compile(":");

	public static double convertTimeInternal(String timeStr) {
		int len = timeStr.length();
		if (len < 4 || len > 9) {
			return 0;
		}
		String[] parts = TIME_SEPARATOR_PATTERN.split(timeStr);

		String secStr;
		switch (parts.length) {
			case 2: secStr = "00"; break;
			case 3: secStr = parts[2]; break;
			default:
				return 0;
		}
		String hourStr = parts[0];
		String minStr = parts[1];
		int hours = parseInt(hourStr);
		int minutes = parseInt(minStr);
		int seconds = parseInt(secStr);

		double totalSeconds = seconds + (minutes + (hours * 60.0)) * 60.0;
		return totalSeconds / (SECONDS_PER_DAY);
	}
}
