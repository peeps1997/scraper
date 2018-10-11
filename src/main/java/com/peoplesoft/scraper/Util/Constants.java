package com.peoplesoft.scraper.Util;

public class Constants {
	public static String BASE_LOGIN_URL = "https://hrprd.allegisgroup.com/psp/HRPRD/EMPLOYEE/HRMS/?&cmd=login&languageCd=ENG";
	public static String TIMESHEET_PENDING_APPROVALS_URL = "https://hrprd.allegisgroup.com/psc/HRPRD/EMPLOYEE/HRMS/c/CAPTURE_TIME_AND_LABOR.TL_MSS_EE_SRCH_PRD.GBL";
	public static String TIMESHEET_REPORTS_URL = "https://hrprd.allegisgroup.com/psc/HRPRD/EMPLOYEE/HRMS/c/ROLE_MANAGER.TL_MSS_EE_SRCH_PRD.GBL";
	public static String BASE_ORIGIN = "https://hrprd.allegisgroup.com";
	public static String BASE_HOST = "hrprd.allegisgroup.com";
	public static String BASE_REFERER = "https://hrprd.allegisgroup.com/psp/HRPRD/EMPLOYEE/HRM";
	public static String DEVICE_FEATURES = "width:1440 height:900 pixelratio:2 touch:0 geolocation:1 websockets:1 webworkers:1 datepicker:1 dtpicker:1 timepicker:1 dnd:1 sessionstorage:1 localstorage:1 history:1 canvas:1 svg:1 postmessage:1 hc:0";
	public static String USERAGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21";
	public static int PEOPLESOFT_TIMEOUT = 20000;
	public static String TIMEZONE_OFFSET = "-330";
	public static String PT_MODE = "f";
	public static String PT_LANG_CD = "ENG";
	public static String PT_INSTALLED_LANG = "GER,DAN,CFR,ENG,FRA,POR,ZHT,JPN,ZHS,POL,ITA,KOR,SVE,ESP,DUT,FIN";
	public static String PT_LANG_SEL = "ENG";

	// ACTIONS & VALUES
	public static String SEARCH_EMPLOYEE = "ICAction";
	public static String SEARCH_EMPLOYEE_VALUE = "TL_MSS_SRCH_WRK_GET_EMPLOYEES";
	public static String SEARCH_PROJECT_ID_VALUE = "VALUE$11";
	public static String SEARCH_DATE_VALUE = "DATE_DAY12";
	public static String SEARCH_EMPLOYEE_ID_VALUE = "VALUE$1";

	// CHOICES
	public static int APPROVED = 1;
	public static int PENDING = 0;
}
