package com.peoplesoft.scraper.DAO;

import java.util.Map;
import java.util.concurrent.Callable;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.peoplesoft.scraper.Util.Constants;
import com.peoplesoft.scraper.model.PeopleSoftLogin;
import com.peoplesoft.scraper.model.TimesheetApproval;
import com.peoplesoft.scraper.model.WeeklyInsight;

public class ComputeCallable  implements Callable<ListenableFuture<TimesheetApproval>> {

	@Override
	public ListenableFuture<TimesheetApproval> call() throws Exception {
		return computeTimesheet();
	}
//	 Connection.Response response;
//	 PeopleSoftLogin dataObject;
	 Map<String, String> loginCookies;
	 Document responseDocument;
	 Element rowLeftHalf;
	 Element rowRightHalf;
	 String date;
	 String userName;
	 String password;
	 String projectID;
	 int count;
	 int choice;
	 public ComputeCallable( Map<String, String> loginCookies,
				String projectID, int count, Element rowLeftHalf, Element rowRightHalf, String date, String userName,
				String password, int choice) {
			super();
			this.loginCookies = loginCookies;
			this.rowLeftHalf = rowLeftHalf;
			this.rowRightHalf = rowRightHalf;
			this.date = date;
			this.userName = userName;
			this.password = password;
			this.projectID = projectID;
			this.count = count;
			this.choice = choice;
		}
	@Async
	public ListenableFuture<TimesheetApproval> computeTimesheet() {
		long startTime = System.nanoTime();
//		System.out.println("Thread ID: "+Thread.currentThread().getId());
		try {
			Connection.Response response;
			if (choice == Constants.APPROVED) {
				response = Jsoup.connect(Constants.TIMESHEET_REPORTS_URL).userAgent(Constants.USERAGENT)
						.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN)
						.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
						.timeout(Constants.PEOPLESOFT_TIMEOUT).data("userid", userName).data("pwd", password)
						.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
						.execute();
				loginCookies.remove("PS_LOGINLIST");
				loginCookies.remove("PS_TOKENEXPIRE");
				loginCookies.remove("PS_TOKEN");
				loginCookies.putAll(response.cookies());
				if (date != null && date.trim().length() > 0) {
					response = Jsoup.connect(Constants.TIMESHEET_REPORTS_URL).userAgent(Constants.USERAGENT)
							.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN)
							.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
							.timeout(Constants.PEOPLESOFT_TIMEOUT)
							.data(Constants.SEARCH_EMPLOYEE, Constants.SEARCH_EMPLOYEE_VALUE)
							.data(Constants.SEARCH_PROJECT_ID_VALUE, projectID).data(Constants.SEARCH_DATE_VALUE, date)
							.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
							.execute();
				} else {
					response = Jsoup.connect(Constants.TIMESHEET_REPORTS_URL).userAgent(Constants.USERAGENT)
							.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN)
							.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
							.timeout(Constants.PEOPLESOFT_TIMEOUT)
							.data(Constants.SEARCH_EMPLOYEE, Constants.SEARCH_EMPLOYEE_VALUE)
							.data(Constants.SEARCH_PROJECT_ID_VALUE, projectID)
							.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
							.execute();
				}

				if(!response.body().isEmpty()) {
				Document responseDocument = Jsoup.parse(response.body());
				String empCount = responseDocument.select("span[class=PSGRIDCOUNTER]").text();
//				System.out.println("(inside computeTimesheet)EMP Count rn: "+ empCount);
//				System.out.println("(inside computeTimesheet)EMP name rn: "+ responseDocument.select("span[id=FIRST_NAME**]").text());
				}
			} else if (choice == Constants.PENDING) {
					response = Jsoup.connect(Constants.TIMESHEET_PENDING_APPROVALS_URL).userAgent(Constants.USERAGENT)
							.cookies(loginCookies ).header("Origin", Constants.BASE_ORIGIN)
							.header("Host", Constants.BASE_HOST).referrer(Constants.BASE_REFERER)
							.timeout(Constants.PEOPLESOFT_TIMEOUT).data("userid", userName).data("pwd", password)
							.header("Content-Type", "application/x-www-form-urlencoded").method(Connection.Method.POST)
							.execute();
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

				if(!response.body().isEmpty()) {
				Document responseDocument = Jsoup.parse(response.body());
				String empCount = responseDocument.select("span[class=PSGRIDCOUNTER]").text();
//				System.out.println("(inside computeTimesheet)EMP Count rn: "+ empCount);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final SettableFuture<TimesheetApproval> timesheetFuture = SettableFuture.create();
		try {
			TimesheetApproval timeObject = new TimesheetApproval();
			timeObject.lastName = rowLeftHalf.select("a[id=LAST_NAME$" + count + "]").text();
			timeObject.firstName = rowLeftHalf.select("span[id=FIRST_NAME$" + count + "]").text();
//			System.out.println("Current Name :"+timeObject.firstName+" "+timeObject.lastName+"  Current Thread ID:"+ Thread.currentThread().getId());
			timeObject.employeeID = rowLeftHalf.select("span[id=EMPLID$" + count + "]").text();
			timeObject.jobTitle = rowRightHalf.select("span[id=JOB_DESCR$" + count + "]").text();
			timeObject.approvalHours = rowRightHalf.select("span[id=TOTAL_PEND_HRS$" + count + "]").text();
			if (choice == Constants.PENDING) {
				timeObject.reportedHours = rowRightHalf.select("span[id=TOTAL_RPTD_HRS$" + count + "]").text();
			} else {
				timeObject.reportedHours = rowRightHalf.select("span[id=TOTAL_RPTD_HRS1$" + count + "]").text();
			}
			timeObject.scheduledHours = rowRightHalf.select("span[id=TOTAL_SCH_HRS$" + count + "]").text();
			try {
				timeObject.absenceHours = rowRightHalf.select("span[id=TL_ABSENCE_LNK$" + count + "]").text();
			} catch (Exception es) {
				timeObject.absenceHours = "0.00";
			}
//			System.out.println("LAST_NAME$" + count);
			timeObject.hoursApproved = rowRightHalf.select("span[id=TOTAL_APRV_HRS$" + count + "]").text();
			timeObject.hoursDenied = rowRightHalf.select("span[id=TOTAL_DENY_HRS$" + count + "]").text();
			Document rowResponseDocument = Jsoup.connect(Constants.TIMESHEET_REPORTS_URL).userAgent(Constants.USERAGENT)
					.cookies(loginCookies).header("Origin", Constants.BASE_ORIGIN).header("Host", Constants.BASE_HOST)
					.referrer(Constants.TIMESHEET_REPORTS_URL).timeout(Constants.PEOPLESOFT_TIMEOUT)
					.data("VALUE$1", timeObject.employeeID).data(Constants.SEARCH_EMPLOYEE, "LAST_NAME$" + count)
					.data(Constants.SEARCH_PROJECT_ID_VALUE, projectID)
					.header("Content-Type", "application/x-www-form-urlencoded").post();
			Elements subRows = rowResponseDocument.select("table[id=tdgblTR_WEEKLY_GRID$0] > tbody > tr");
			WeeklyInsight weekReport = new WeeklyInsight();
//			System.out.println("Rsize " + subRows.size());
			for (int j = 0; j < subRows.size(); j++) {
				Element subRow = subRows.get(j);
				try {
					if (subRow.selectFirst("span[id=QTY_DAY1$" + j + "]").text().trim().length() > 1) {
						weekReport.sunday = subRow.selectFirst("span[id=QTY_DAY1$" + j + "]").text();
						if (choice == Constants.APPROVED) {
							try {
								if (subRow.selectFirst("select[id=TRC$" + j + "] option[selected]").text().trim()
										.length() > 1) {
									weekReport.sundayCode = subRow
											.selectFirst("select[id=TRC$" + j + "] option[selected]").text();
								}
							} catch (Exception e) {
								weekReport.sundayCode = subRow.selectFirst("span[id=TRC$" + j + "]").text();
							}
						} else {
							weekReport.sundayCode = subRow.selectFirst("select[id=TRC$" + j + "] option[selected]")
									.text();
						}
					}
				} catch (Exception ignored) {
				}
				try {
					if (subRow.selectFirst("span[id=QTY_DAY2$" + j + "]").text().trim().length() > 1) {
						weekReport.monday = subRow.selectFirst("span[id=QTY_DAY2$" + j + "]").text();
						if (choice == Constants.APPROVED) {
							try {
								if (subRow.selectFirst("select[id=TRC$" + j + "] option[selected]").text().trim()
										.length() > 1) {
									weekReport.mondayCode = subRow
											.selectFirst("select[id=TRC$" + j + "] option[selected]").text();
								}
							} catch (Exception e) {
								weekReport.mondayCode = subRow.selectFirst("span[id=TRC$" + j + "]").text();
							}
						} else {
							weekReport.mondayCode = subRow.selectFirst("select[id=TRC$" + j + "] option[selected]")
									.text();
						}
					}
				} catch (Exception ignored) {
				}
				try {
					if (subRow.selectFirst("span[id=QTY_DAY3$" + j + "]").text().trim().length() > 1) {
						weekReport.tuesday = subRow.selectFirst("span[id=QTY_DAY3$" + j + "]").text();
						if (choice == Constants.APPROVED) {
							try {
								if (subRow.selectFirst("select[id=TRC$" + j + "] option[selected]").text().trim()
										.length() > 1) {
									weekReport.tuesdayCode = subRow
											.selectFirst("select[id=TRC$" + j + "] option[selected]").text();
								}
							} catch (Exception e) {
								weekReport.tuesdayCode = subRow.selectFirst("span[id=TRC$" + j + "]").text();
							}
						} else {
							weekReport.tuesdayCode = subRow.selectFirst("select[id=TRC$" + j + "] option[selected]")
									.text();
						}
					}
				} catch (Exception ignored) {
				}
				try {
					if (subRow.selectFirst("span[id=QTY_DAY4$" + j + "]").text().trim().length() > 1) {
						weekReport.wednesday = subRow.selectFirst("span[id=QTY_DAY4$" + j + "]").text();
						if (choice == Constants.APPROVED) {
							try {
								if (subRow.selectFirst("select[id=TRC$" + j + "] option[selected]").text().trim()
										.length() > 1) {
									weekReport.wednesdayCode = subRow
											.selectFirst("select[id=TRC$" + j + "] option[selected]").text();
								}
							} catch (Exception e) {
								weekReport.wednesdayCode = subRow.selectFirst("span[id=TRC$" + j + "]").text();
							}
						} else {
							weekReport.wednesdayCode = subRow.selectFirst("select[id=TRC$" + j + "] option[selected]")
									.text();
						}
					}
				} catch (Exception ignored) {
				}
				try {
					if (subRow.selectFirst("span[id=QTY_DAY5$" + j + "]").text().trim().length() > 1) {
						weekReport.thursday = subRow.selectFirst("span[id=QTY_DAY5$" + j + "]").text();
						if (choice == Constants.APPROVED) {
							try {
								if (subRow.selectFirst("select[id=TRC$" + j + "] option[selected]").text().trim()
										.length() > 1) {
									weekReport.thursdayCode = subRow
											.selectFirst("select[id=TRC$" + j + "] option[selected]").text();
								}
							} catch (Exception e) {
								weekReport.thursdayCode = subRow.selectFirst("span[id=TRC$" + j + "]").text();
							}
						} else {
							weekReport.thursdayCode = subRow.selectFirst("select[id=TRC$" + j + "] option[selected]")
									.text();
						}
					}
				} catch (Exception ignored) {
				}
				try {
					if (subRow.selectFirst("span[id=QTY_DAY6$" + j + "]").text().trim().length() > 1) {
						weekReport.friday = subRow.selectFirst("span[id=QTY_DAY6$" + j + "]").text();
						if (choice == Constants.APPROVED) {
							try {
								if (subRow.selectFirst("select[id=TRC$" + j + "] option[selected]").text().trim()
										.length() > 1) {
									weekReport.fridayCode = subRow
											.selectFirst("select[id=TRC$" + j + "] option[selected]").text();
								}
							} catch (Exception e) {
								weekReport.fridayCode = subRow.selectFirst("span[id=TRC$" + j + "]").text();
							}
						} else {
							weekReport.fridayCode = subRow.selectFirst("select[id=TRC$" + j + "] option[selected]")
									.text();
						}
					}
				} catch (Exception ignored) {
				}
				try {
					if (subRow.selectFirst("span[id=QTY_DAY7$" + j + "]").text().trim().length() > 1) {
						weekReport.saturday = subRow.selectFirst("span[id=QTY_DAY7$" + j + "]").text();
						if (choice == Constants.APPROVED) {
							try {
								if (subRow.selectFirst("select[id=TRC$" + j + "] option[selected]").text().trim()
										.length() > 1) {
									weekReport.saturdayCode = subRow
											.selectFirst("select[id=TRC$" + j + "] option[selected]").text();
								}
							} catch (Exception e) {
								weekReport.saturdayCode = subRow.selectFirst("span[id=TRC$" + j + "]").text();
							}
						} else {
							weekReport.saturdayCode = subRow.selectFirst("select[id=TRC$" + j + "] option[selected]")
									.text();
						}
					}
				} catch (Exception ignored) {
				}
			}
			try {
				if (weekReport.sunday == null || weekReport.sunday.trim().equals("")) {
					weekReport.sunday = "N/A";
					weekReport.sundayCode = "N/A";
				}
				if (weekReport.monday == null || weekReport.monday.trim().equals("")) {
					weekReport.monday = "N/A";
					weekReport.mondayCode = "N/A";
				}
				if (weekReport.tuesday == null || weekReport.tuesday.trim().equals("")) {
					weekReport.tuesday = "N/A";
					weekReport.tuesdayCode = "N/A";
				}
				if (weekReport.wednesday == null || weekReport.wednesday.trim().equals("")) {
					weekReport.wednesday = "N/A";
					weekReport.wednesdayCode = "N/A";
				}
				if (weekReport.thursday == null || weekReport.thursday.trim().equals("")) {
					weekReport.thursday = "N/A";
					weekReport.thursdayCode = "N/A";
				}
				if (weekReport.friday == null || weekReport.friday.trim().equals("")) {
					weekReport.friday = "N/A";
					weekReport.fridayCode = "N/A";
				}
				if (weekReport.saturday == null || weekReport.saturday.trim().equals("")) {
					weekReport.saturday = "N/A";
					weekReport.saturdayCode = "N/A";
				}
//				System.out.println("Sub" + count + ": " + ((double) ((System.nanoTime() - startTime) / 1000000000.0)));
			} catch (Exception ignored) {
			}
			timeObject.report = weekReport;
			timesheetFuture.set(timeObject);
		} catch (Exception e) {
			e.printStackTrace();
			timesheetFuture.set(null);
		}
		return timesheetFuture;
	}


}
