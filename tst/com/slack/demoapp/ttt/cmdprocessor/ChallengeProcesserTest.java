package com.slack.demoapp.ttt.cmdprocessor;

import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import allbegray.slack.type.Channel;
import allbegray.slack.webapi.SlackWebApiClient;

import com.slack.demoapp.ttt.cmdprocessor.ChallengeProcessor;
import com.slack.demoapp.ttt.db.IAppDBManager;
import com.slack.demoapp.ttt.exception.InternalError;
import com.slack.demoapp.ttt.model.Game;

/**
 * Tests the challenge command
 * 
 */
public class ChallengeProcesserTest {

	@InjectMocks
	ChallengeProcessor challengeProcessor = new ChallengeProcessor();
	
	@Mock
	private IAppDBManager dbManager;
	
	@BeforeMethod(alwaysRun = true)
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	void testGameInChannel() throws InternalError, SQLException {
		when(dbManager.fetchGameInChannel("channel_id")).thenReturn(new Game());
		HttpServletResponse mockedResp = mock(HttpServletResponse.class);
		SlackWebApiClient mockSlackClient = mock(SlackWebApiClient.class);
		challengeProcessor.process("channel_id", "challenge <@user|user>", mockSlackClient, "user_id", "user_name", mockedResp);
		verify(mockSlackClient).postMessage(Mockito.eq("channel_id"), Mockito.eq("Another game is in progress in channel. You can see status of game using /ttt show"));
	}
	
	@Test
	void testSelfChallenge() throws InternalError, SQLException {
		when(dbManager.fetchGameInChannel("channel_id")).thenReturn(null);
		HttpServletResponse mockedResp = mock(HttpServletResponse.class);
		SlackWebApiClient mockSlackClient = mock(SlackWebApiClient.class);
		challengeProcessor.process("channel_id", "challenge <@user_id|username>", mockSlackClient, "user_id", "user_name", mockedResp);
		verify(mockSlackClient).postMessage(Mockito.eq("channel_id"), Mockito.eq("C'mmon, play with someone other than yourself!"));
	}
	
	@Test
	void testNewCreateGame() throws InternalError, SQLException {
		when(dbManager.fetchGameInChannel("channel_id")).thenReturn(null);
		HttpServletResponse mockedResp = mock(HttpServletResponse.class);
		SlackWebApiClient mockSlackClient = mock(SlackWebApiClient.class);
		java.util.List<String> users = new ArrayList<>();
		users.add("testuser_id");
		Channel ch = new Channel();
		ch.setMembers(users);
		when(mockSlackClient.getChannelInfo("channel_id")).thenReturn(ch);
		challengeProcessor.process("channel_id", "challenge <@testuser_id|testusername>", mockSlackClient, "user_id", "user_name", mockedResp);
		verify(dbManager).createNewGame(Mockito.eq("channel_id"), 
				Mockito.eq("user_id"), Mockito.eq("testuser_id"));
	}
	
}
