package com.peoplesoft.scraper.model;

public class TimesheetApproval {
	public String firstName;
	public String lastName;
	public String employeeID;
	public String jobTitle;
	public String approvalHours;
	public String reportedHours;
	public String scheduledHours;
	public String absenceHours;
	public String hoursApproved;
	public String hoursDenied;

	public WeeklyInsight report;

	public TimesheetApproval() {

	}

	public TimesheetApproval(String firstName, String lastName, String employeeID, String jobTitle,
			String approvalHours, String reportedHours, String scheduledHours, String absenceHours,
			String hoursApproved, String hoursDenied, WeeklyInsight report) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.employeeID = employeeID;
		this.jobTitle = jobTitle;
		this.approvalHours = approvalHours;
		this.reportedHours = reportedHours;
		this.scheduledHours = scheduledHours;
		this.absenceHours = absenceHours;
		this.hoursApproved = hoursApproved;
		this.hoursDenied = hoursDenied;
		this.report = report;
	}

}
