package com.peoplesoft.scraper.Controller;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.peoplesoft.scraper.DAO.PeopleSoftDAO;
import com.peoplesoft.scraper.model.PeopleSoftLogin;
import com.peoplesoft.scraper.model.Response;
import com.peoplesoft.scraper.model.TimesheetApproval;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class Controller {
	ResponseEntity<Response> response;
	ResponseEntity<LinkedList<TimesheetApproval>> timesheetResponse;

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Response> login(@RequestParam("userName") String userName,
			@RequestParam("password") String password) {
		ListenableFuture<PeopleSoftLogin> loginFuture = JdkFutureAdapters
				.listenInPoolThread(PeopleSoftDAO.tryLogin(userName, password));
		Futures.addCallback(loginFuture, new FutureCallback<PeopleSoftLogin>() {
			public void onSuccess(@Nullable PeopleSoftLogin result) {
				if (result != null) {
					if (result.isPasswordValid) {
						response = new ResponseEntity<Response>(new Response("success"), HttpStatus.ACCEPTED);
					} else {
						response = new ResponseEntity<Response>(new Response("failure"), HttpStatus.FORBIDDEN);
					}
				} else {
					response = new ResponseEntity<Response>(new Response("failure"), HttpStatus.FORBIDDEN);
				}
			}

			@Override
			public void onFailure(@Nonnull Throwable t) {
				response = new ResponseEntity<Response>(new Response("failure"), HttpStatus.FORBIDDEN);
			}
		});
		return response;
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/approved-reports", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<LinkedList<TimesheetApproval>> searchApproved(@RequestParam("userName") String userName,
			@RequestParam("password") String password, @RequestParam("projectID") String projectID,
			@RequestParam("date") String date) {
		Long t1 = System.currentTimeMillis();
//		System.out.println("Request Started: " + t1);
		ListenableFuture<LinkedList<TimesheetApproval>> employeeFuture = JdkFutureAdapters.listenInPoolThread(
				PeopleSoftDAO.searchEmployeesApprovedReportsViaProject(projectID, date, userName, password));
//		System.out.println("Controller Thread ID: "+Thread.currentThread().getId());
		Futures.addCallback(employeeFuture, new FutureCallback<LinkedList<TimesheetApproval>>() {

			@Override
			public void onSuccess(LinkedList<TimesheetApproval> result) {
				if (result != null && result.size() != 0) {
					timesheetResponse = new ResponseEntity<LinkedList<TimesheetApproval>>(result, HttpStatus.ACCEPTED);
				} else {
					timesheetResponse = new ResponseEntity<LinkedList<TimesheetApproval>>(
							new LinkedList<TimesheetApproval>(), HttpStatus.ACCEPTED);
				}
			}

			@Override
			public void onFailure(Throwable t) {
				timesheetResponse = new ResponseEntity<LinkedList<TimesheetApproval>>(
						new LinkedList<TimesheetApproval>(), HttpStatus.FORBIDDEN);
			}
		});
		return timesheetResponse;
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/pending-reports", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<LinkedList<TimesheetApproval>> searchPending(@RequestParam("userName") String userName,
			@RequestParam("password") String password, @RequestParam("projectID") String projectID,
			@RequestParam("date") String date) {
		ListenableFuture<LinkedList<TimesheetApproval>> employeeFuture = JdkFutureAdapters.listenInPoolThread(
				PeopleSoftDAO.searchEmployeesPendingReportsViaProject(projectID, date, userName, password));
		Futures.addCallback(employeeFuture, new FutureCallback<LinkedList<TimesheetApproval>>() {

			@Override
			public void onSuccess(LinkedList<TimesheetApproval> result) {
				if (result != null && result.size() != 0) {
					timesheetResponse = new ResponseEntity<LinkedList<TimesheetApproval>>(result, HttpStatus.ACCEPTED);
				} else {
					timesheetResponse = new ResponseEntity<LinkedList<TimesheetApproval>>(
							new LinkedList<TimesheetApproval>(), HttpStatus.ACCEPTED);
				}
			}

			@Override
			public void onFailure(Throwable t) {
				timesheetResponse = new ResponseEntity<LinkedList<TimesheetApproval>>(
						new LinkedList<TimesheetApproval>(), HttpStatus.FORBIDDEN);
			}
		});
		return timesheetResponse;
	}
}