package com.slack.demoapp.ttt.cmdprocessor;

import static org.mockito.Mockito.*;

import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import allbegray.slack.webapi.SlackWebApiClient;

import com.slack.demoapp.ttt.db.IAppDBManager;
import com.slack.demoapp.ttt.exception.InternalError;
import com.slack.demoapp.ttt.model.Game;

public class QuitProcesserTest {

	@InjectMocks
	QuitProcessor quitProcessor = new QuitProcessor();
	
	@Mock
	private IAppDBManager dbManager;
	
	@BeforeMethod(alwaysRun = true)
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	void testNoGameInChannel() throws InternalError, SQLException {
		when(dbManager.fetchGameInChannel("channel_id")).thenReturn(null);
		HttpServletResponse mockedResp = mock(HttpServletResponse.class);
		SlackWebApiClient mockSlackClient = mock(SlackWebApiClient.class);
		quitProcessor.process("channel_id", "quit", mockSlackClient, "user_id", "user_name", mockedResp);
		verify(mockSlackClient).postMessage(Mockito.eq("channel_id"), Mockito.eq("There is no game in progress in channel. You can see status new game using /ttt challenge @user"));
	}

	@Test
	void testQuit() throws InternalError, SQLException {
		Game game = Game.builder().board("---------")
				.challengeeId("testChallengeeId")
				.challengerId("testChallengerId")
				.channeld("channel_Id")
				.nextPlayerId("testChallengerId")
				.build();
		when(dbManager.fetchGameInChannel("channel_id")).thenReturn(game);
		SlackWebApiClient mockSlackClient = mock(SlackWebApiClient.class);
		HttpServletResponse mockedResp = mock(HttpServletResponse.class);
		quitProcessor.process("channel_id", "quit", mockSlackClient, "testChallengerId", "user_name", mockedResp);
		verify(dbManager).deleteGame("channel_id");
	}
}
