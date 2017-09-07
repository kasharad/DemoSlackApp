package com.slack.demoapp.servlet;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import allbegray.slack.webapi.SlackWebApiClient;

import com.slack.demoapp.ttt.cmdprocessor.ChallengeProcessor;
import com.slack.demoapp.ttt.cmdprocessor.MoveProcessor;
import com.slack.demoapp.ttt.cmdprocessor.ShowProcessor;
import com.slack.demoapp.ttt.db.IAppDBManager;
import com.slack.demoapp.ttt.exception.InternalError;
import com.slack.demoapp.ttt.model.SlackApp;

/**
 * Tests for the basic commands using mocks.
 *
 */
public class TicTacToeServletTest {

	@InjectMocks
	TicTacToeCommandServlet testServlet = new TicTacToeCommandServlet();
	
	@Mock
	private IAppDBManager dbManager;
	
	@Mock
	private ChallengeProcessor challengeProcessor;
	
	@Mock
	private MoveProcessor moveProcessor;
	
	@Mock
	private ShowProcessor showProcessor;
	
	@Mock
	private SlackApp slackApp;
	
	@BeforeMethod(alwaysRun = true)
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testInvalidSlackToken() throws IOException {
		
		HttpServletRequest mocked = (HttpServletRequest) mock(HttpServletRequest.class);
		HttpServletResponse mockedResp = mock(HttpServletResponse.class);
		when(mocked.getParameter("text")).thenReturn("challenge <@test|test>");
		when(mocked.getParameter("token")).thenReturn(null);
		testServlet.doPost(mocked, mockedResp);
		verify(mockedResp).sendError(Mockito.eq(HttpStatus.BAD_REQUEST_400),
				Mockito.anyString());
	}
	
	@Test
	public void testChallenge() throws IOException, InternalError, SQLException {
		
		HttpServletRequest mocked = (HttpServletRequest) mock(HttpServletRequest.class);
		HttpServletResponse mockedResp = mock(HttpServletResponse.class);
		
		when(mocked.getParameter("text")).thenReturn("challenge <@test|test>");
		when(mocked.getParameter("token")).thenReturn("someCode");
		when(mocked.getParameter("user_id")).thenReturn("user_id");
		when(mocked.getParameter("user_name")).thenReturn("user_name");
		when(mocked.getParameter("channel_id")).thenReturn("ch_id");
		
		when(dbManager.getAuthToken("user_id")).thenReturn("authtoken");
		when(slackApp.getVerificationCode()).thenReturn("someCode");
		testServlet.doPost(mocked, mockedResp);
		verify(challengeProcessor).process(Mockito.eq("ch_id"), 
				Mockito.eq("challenge <@test|test>"), Mockito.any(SlackWebApiClient.class), 
				Mockito.eq("user_id"), Mockito.eq("user_name"), Mockito.any(HttpServletResponse.class));
	}
	
	@Test
	public void testMove() throws IOException, InternalError, SQLException {
		
		HttpServletRequest mocked = (HttpServletRequest) mock(HttpServletRequest.class);
		HttpServletResponse mockedResp = mock(HttpServletResponse.class);
		when(mocked.getParameter("text")).thenReturn("move 2 3");
		when(mocked.getParameter("token")).thenReturn("someCode");
		when(mocked.getParameter("user_id")).thenReturn("user_id");
		when(mocked.getParameter("user_name")).thenReturn("user_name");
		when(mocked.getParameter("channel_id")).thenReturn("ch_id");
		
		when(dbManager.getAuthToken("user_id")).thenReturn("authtoken");
		when(slackApp.getVerificationCode()).thenReturn("someCode");

		testServlet.doPost(mocked, mockedResp);
		verify(moveProcessor).process(Mockito.eq("ch_id"), 
				Mockito.eq("move 2 3"), Mockito.any(SlackWebApiClient.class), 
				Mockito.eq("user_id"), Mockito.eq("user_name"), Mockito.any(HttpServletResponse.class));
	}
	
	@Test
	public void testShow() throws IOException, InternalError, SQLException {
		
		HttpServletRequest mocked = (HttpServletRequest) mock(HttpServletRequest.class);
		HttpServletResponse mockedResp = mock(HttpServletResponse.class);
		when(mocked.getParameter("text")).thenReturn("show");
		when(mocked.getParameter("token")).thenReturn("someCode");
		when(mocked.getParameter("user_id")).thenReturn("user_id");
		when(mocked.getParameter("user_name")).thenReturn("user_name");
		when(mocked.getParameter("channel_id")).thenReturn("ch_id");
		
		when(dbManager.getAuthToken("user_id")).thenReturn("authtoken");
		when(slackApp.getVerificationCode()).thenReturn("someCode");

		testServlet.doPost(mocked, mockedResp);
		verify(showProcessor).process(Mockito.eq("ch_id"), 
				Mockito.eq("show"), Mockito.any(SlackWebApiClient.class), 
				Mockito.eq("user_id"), Mockito.eq("user_name"), Mockito.any(HttpServletResponse.class));
	}
}
