package com.slack.demoapp.ttt.cmdprocessor;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import allbegray.slack.webapi.SlackWebApiClient;

import com.slack.demoapp.ttt.db.IAppDBManager;
import com.slack.demoapp.ttt.exception.InternalError;
import com.slack.demoapp.ttt.model.Game;

@Component(QuitProcessor.CONTEXT)
public class QuitProcessor implements IProcessor {

	public static final String CONTEXT = "quitProcessor";

	@Autowired
	private IAppDBManager dbManager;

	static Pattern quitPattern = Pattern
			.compile("quit$");

	@Override
	public void process(String channel, String cmd,
			SlackWebApiClient webApiClient, String userId, String username,
			HttpServletResponse response) throws SQLException, InternalError {

		// Check game is going on!
		Game game = dbManager.fetchGameInChannel(channel);
		if (game == null) {
			webApiClient
					.postMessage(
							channel,
							"There is no game in progress in channel. You can see status new game using /ttt challenge @user");
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		// Check user is entitiled to quit
		if (!game.getChallengeeId().equals(userId) && !game.getChallengerId().equals(userId)) {
			webApiClient
					.postMessage(channel,
							"You can only quit when you are playing the game");
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		Matcher m = quitPattern.matcher(cmd);
		if (m.find()) {
			dbManager.deleteGame(channel);
			webApiClient.postMessage(channel, "Game deleted by @" + username);
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		} else {
			showHelp(userId, webApiClient);
			return;
		}
	}


	@Override
	public void showHelp(String userId, SlackWebApiClient webApiClient) {
		String s = "Usage: /ttt quit\n";
		String channelId = webApiClient.openDirectMessageChannel(userId);
		webApiClient.postMessage(channelId, s);
		webApiClient.closeDirectMessageChannel(channelId);
	}

}
