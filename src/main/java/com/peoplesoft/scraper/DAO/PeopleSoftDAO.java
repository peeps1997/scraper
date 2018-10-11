package com.peoplesoft.scraper.DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.peoplesoft.scraper.Util.Constants;
import com.peoplesoft.scraper.model.PeopleSoftLogin;
import com.peoplesoft.scraper.model.TimesheetApproval;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;

public class PeopleSoftDAO {
	static ComputeCallable computeTS;
	static Connection.Response response;
	static PeopleSoftLogin dataObject;
	static Map<String, String> loginCookies;
	static Document responseDocument;
	static Elements rowLeftHalf;
	static Elements rowsRightHalf;
	static String date;
	static String userName;
	static String password;
	static String projectID;

	@Async
	public static ListenableFuture<PeopleSoftLogin> tryLogin(String userNameX, final String passwordX) {
		final SettableFuture<PeopleSoftLogin> loginFuture = SettableFuture.create();
		userName = userNameX;
		password = passwordX;
		try {
			dataObject = new PeopleSoftLogin();
			response = Jsoup.connect(Constants.BASE_LOGIN_URL).userAgent(Constants.USERAGENT)
					.timeout(Constants.PEOPLESOFT_TIMEOUT).method(Connection.Method.GET).execute();
			dataObject.loginCookies = response.cookies();
			dataObject.loginCookies.put("PS_DEVICEFEATURES", "");
			dataObject.loginCookies.put("PS_LOGINLIST", "-1");
			dataObject.loginCookies.put("PS_TOKENEXPIRE", "-1");
			response = Jsoup.connect(Constants.BASE_LOGIN_URL).userAgent(Constants.USERAGENT)
					.cookies(dataObject.loginCookies).header("Origin", Constants.BASE_ORIGIN)
					.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
					.timeout(Constants.PEOPLESOFT_TIMEOUT).data("userid", userName).data("pwd", password)
					.followRedirects(true).header("Content-Type", "application/x-www-form-urlencoded")
					.method(Connection.Method.POST).execute();
			dataObject.loginCookies.remove("PS_LOGINLIST");
			dataObject.loginCookies.remove("PS_TOKENEXPIRE");
			dataObject.loginCookies.remove("PS_TOKEN");
			dataObject.loginCookies.putAll(response.cookies());
			String checkResponse = response.parse().toString().toLowerCase();
			if (checkResponse.contains("peoplesoft sign-in")) {
				dataObject.isPasswordValid = false;
				loginFuture.set(dataObject);
			} else if (checkResponse.contains("employee-facing registry")) {
				dataObject.isPasswordValid = true;
				loginFuture.set(dataObject);
			} else {
				dataObject.isPasswordValid = false;
				loginFuture.set(dataObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
			loginFuture.set(null);
		}

		return loginFuture;
	}

	@Async
	public static ListenableFuture<LinkedList<TimesheetApproval>> searchEmployeesPendingReportsViaProject(
			String projectIDX, String dateX, String userNameX, String passwordX) {
		final SettableFuture<LinkedList<TimesheetApproval>> employeeFuture = SettableFuture.create();
		try {
			userName = userNameX;
			password = passwordX;
			date = dateX;
			projectID = projectIDX;
			loginCookies = new HashMap<String, String>();
			Connection.Response response;
			LinkedList<TimesheetApproval> employeeData = new LinkedList<TimesheetApproval>();
			loginCookies.put("PS_DEVICEFEATURES", "");
			long startTime = System.nanoTime();
			if (date != null && date.trim().length() > 0) {
				response = Jsoup.connect(Constants.TIMESHEET_PENDING_APPROVALS_URL).userAgent(Constants.USERAGENT)
						.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN)
						.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
						.timeout(Constants.PEOPLESOFT_TIMEOUT).data("userid", userName).data("pwd", password)
						.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
						.execute();
			} else {
				response = Jsoup.connect(Constants.TIMESHEET_PENDING_APPROVALS_URL).userAgent(Constants.USERAGENT)
						.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN)
						.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
						.timeout(Constants.PEOPLESOFT_TIMEOUT).data("userid", userName).data("pwd", password)
						.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
						.execute();
			}
//			System.out.println("1: " + ((double) ((System.nanoTime() - startTime) / 1000000000.0)));
			startTime = System.nanoTime();
			loginCookies.remove("PS_LOGINLIST");
			loginCookies.remove("PS_TOKENEXPIRE");
			loginCookies.remove("PS_TOKEN");
			loginCookies.putAll(response.cookies());
			if (date != null && date.trim().length() > 0) {
				response = Jsoup.connect(Constants.TIMESHEET_PENDING_APPROVALS_URL).userAgent(Constants.USERAGENT)
						.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN)
						.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
						.timeout(Constants.PEOPLESOFT_TIMEOUT)
						.data(Constants.SEARCH_EMPLOYEE, Constants.SEARCH_EMPLOYEE_VALUE)
						.data(Constants.SEARCH_PROJECT_ID_VALUE, projectID).data(Constants.SEARCH_DATE_VALUE, date)
						.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
						.execute();
			} else {
				response = Jsoup.connect(Constants.TIMESHEET_PENDING_APPROVALS_URL).userAgent(Constants.USERAGENT)
						.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN)
						.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
						.timeout(Constants.PEOPLESOFT_TIMEOUT)
						.data(Constants.SEARCH_EMPLOYEE, Constants.SEARCH_EMPLOYEE_VALUE)
						.data(Constants.SEARCH_PROJECT_ID_VALUE, projectID)
						.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
						.execute();
			}
			Document responseDocument = Jsoup.parse(response.body());
			rowLeftHalf = responseDocument.select("table[id=tdgblTL_MGR_SRCH_VW$0] > tbody > tr");
			rowsRightHalf = responseDocument.select("table[id=tdgbrTL_MGR_SRCH_VW$0] > tbody > tr");
			for (int i = 0; i < rowLeftHalf.size(); i++) {
//				System.out.println("(inside computeTimesheet)EMP name rn: "+ responseDocument.select("a[id=LAST_NAME$" + i + "]").text());
			}
			int count = 0;
			if (rowLeftHalf.size() != 0) {
				ExecutorService executorX = Executors.newFixedThreadPool(rowLeftHalf.size());
				List<Future<ListenableFuture<TimesheetApproval>>> futures = new ArrayList<Future<ListenableFuture<TimesheetApproval>>>();
				ArrayList<ComputeCallable> computeMulti = new ArrayList<ComputeCallable>();
				for (int i = 0; i < rowLeftHalf.size(); i++) {
					computeTS = new ComputeCallable(loginCookies, projectID, count, rowLeftHalf.get(i),
							rowsRightHalf.get(i), date, userName, password, Constants.APPROVED);
					computeMulti.add(computeTS);
					count++;
				}
				futures = executorX.invokeAll(computeMulti);
				for (Future<ListenableFuture<TimesheetApproval>> future : futures) {
					try {
						if (future.get().get() != null) {
							employeeData.add(future.get().get());
						}
					} catch (Exception ignored) {
					}
				}
				if (employeeData != null && employeeData.size() != 0)
					employeeFuture.set(employeeData);
				else
					employeeFuture.set(null);
			} else {
				employeeFuture.set(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			employeeFuture.set(null);
		}

		return employeeFuture;
	}

	@Async
	public static ListenableFuture<LinkedList<TimesheetApproval>> searchEmployeesApprovedReportsViaProject(
			String projectIDX, String dateX, String userNameX, String passwordX) {
//		System.out.println("Approved reports Started : "+ System.currentTimeMillis()/1000);
		userName = userNameX;
		password = passwordX;
		date = dateX;
		projectID = projectIDX;
		loginCookies = new HashMap<String, String>();
		final SettableFuture<LinkedList<TimesheetApproval>> employeeFuture = SettableFuture.create();
		try {
			Connection.Response response;
			LinkedList<TimesheetApproval> employeeData = new LinkedList<TimesheetApproval>();
			loginCookies.put("PS_DEVICEFEATURES", "");
			long startTime = System.nanoTime();
			if (date != null && date.trim().length() > 0) {
//				System.out.println("First Connection Started : "+ System.currentTimeMillis()/1000);
				response = Jsoup.connect(Constants.TIMESHEET_REPORTS_URL).userAgent(Constants.USERAGENT)
						.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN)
						.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
						.timeout(Constants.PEOPLESOFT_TIMEOUT).data("userid", userName).data("pwd", password)
						.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
						.execute();
			} else {
//				System.out.println("First Connection Started : "+ System.currentTimeMillis()/1000);
				response = Jsoup.connect(Constants.TIMESHEET_REPORTS_URL).userAgent(Constants.USERAGENT)
						.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN)
						.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
						.timeout(Constants.PEOPLESOFT_TIMEOUT).data("userid", userName).data("pwd", password)
						.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
						.execute();
			}
//			System.out.println("First Connection Response received : "+ System.currentTimeMillis()/1000);
			startTime = System.nanoTime();
			loginCookies.remove("PS_LOGINLIST");
			loginCookies.remove("PS_TOKENEXPIRE");
			loginCookies.remove("PS_TOKEN");
			loginCookies.putAll(response.cookies());
			if (date != null && date.trim().length() > 0) {
//				System.out.println("Second Connection Started : "+ System.currentTimeMillis()/1000);
				response = Jsoup.connect(Constants.TIMESHEET_REPORTS_URL).userAgent(Constants.USERAGENT)
						.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN)
						.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
						.timeout(Constants.PEOPLESOFT_TIMEOUT)
						.data(Constants.SEARCH_EMPLOYEE, Constants.SEARCH_EMPLOYEE_VALUE)
						.data(Constants.SEARCH_PROJECT_ID_VALUE, projectID).data(Constants.SEARCH_DATE_VALUE, date)
						.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
						.execute();
			} else {
//				System.out.println("Second Connection Started : "+ System.currentTimeMillis()/1000);
				response = Jsoup.connect(Constants.TIMESHEET_REPORTS_URL).userAgent(Constants.USERAGENT)
						.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN)
						.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
						.timeout(Constants.PEOPLESOFT_TIMEOUT)
						.data(Constants.SEARCH_EMPLOYEE, Constants.SEARCH_EMPLOYEE_VALUE)
						.data(Constants.SEARCH_PROJECT_ID_VALUE, projectID)
						.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
						.execute();
			}
//			System.out.println("Second Connection Response Received : "+ System.currentTimeMillis()/1000);
			Document responseDocument = Jsoup.parse(response.body());
			rowLeftHalf = responseDocument.select("table[id=tdgblTL_MGR_SRCH_VW$0] > tbody > tr");
			rowsRightHalf = responseDocument.select("table[id=tdgbrTL_MGR_SRCH_VW$0] > tbody > tr");
			int count = 0;
			if (rowLeftHalf.size() != 0) {
				ExecutorService executorX = Executors.newFixedThreadPool(rowLeftHalf.size());
				List<Future<ListenableFuture<TimesheetApproval>>> futures = new ArrayList<Future<ListenableFuture<TimesheetApproval>>>();
				ArrayList<ComputeCallable> computeMulti = new ArrayList<ComputeCallable>();
				for (int i = 0; i < rowLeftHalf.size(); i++) {
					computeTS = new ComputeCallable(loginCookies, projectID, count, rowLeftHalf.get(i),
							rowsRightHalf.get(i), date, userName, password, Constants.APPROVED);
					computeMulti.add(computeTS);
					count++;
				}
//				System.out.println("Invoking all threads : "+ System.currentTimeMillis()/1000);
				futures = executorX.invokeAll(computeMulti);
//				System.out.println("All thread processes ended : "+ System.currentTimeMillis()/1000);
				for (Future<ListenableFuture<TimesheetApproval>> future : futures) {
					try {
						if (future.get() != null) {
							employeeData.add(future.get().get());
						}
					} catch (Exception ignored) {
					}
				}
				if (employeeData != null && employeeData.size() != 0)
					employeeFuture.set(employeeData);
				else
					employeeFuture.set(null);
			} else {
				employeeFuture.set(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			employeeFuture.set(null);

		}
		return employeeFuture;
	}
}
