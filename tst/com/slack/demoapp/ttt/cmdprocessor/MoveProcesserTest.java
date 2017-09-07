package com.slack.demoapp.ttt.cmdprocessor;

import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import lombok.Builder;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import allbegray.slack.type.Channel;
import allbegray.slack.type.User;
import allbegray.slack.webapi.SlackWebApiClient;

import com.slack.demoapp.ttt.cmdprocessor.ChallengeProcessor;
import com.slack.demoapp.ttt.db.IAppDBManager;
import com.slack.demoapp.ttt.exception.InternalError;
import com.slack.demoapp.ttt.model.Game;

public class MoveProcesserTest {

	@InjectMocks
	MoveProcessor moveProcessor = new MoveProcessor();
	
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
		moveProcessor.process("channel_id", "move 2 3", mockSlackClient, "user_id", "user_name", mockedResp);
		verify(mockSlackClient).postMessage(Mockito.eq("channel_id"), Mockito.eq("There is no game in progress in channel. You can see status new game using /ttt challenge @user"));
	}

	@Test
	void testValidMove() throws InternalError, SQLException {
		Game game = Game.builder().board("---------")
				.challengeeId("testChallengeeId")
				.challengerId("testChallengerId")
				.channeld("channel_Id")
				.nextPlayerId("testChallengerId")
				.build();
		when(dbManager.fetchGameInChannel("channel_id")).thenReturn(game);
		SlackWebApiClient mockSlackClient = mock(SlackWebApiClient.class);
		HttpServletResponse mockedResp = mock(HttpServletResponse.class);
		when(mockSlackClient.getUserInfo("testChallengeeId")).thenReturn(new User());
		moveProcessor.process("channel_id", "move 2 3", mockSlackClient, "testChallengerId", "user_name", mockedResp);
		verify(dbManager).updateGame(Mockito.eq("channel_id"), 
				Mockito.eq("-----O---"), 
				Mockito.eq("testChallengeeId"));
		
	}
}
