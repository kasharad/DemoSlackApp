package com.slack.demoapp.ttt.cmdprocessor;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import allbegray.slack.type.User;
import allbegray.slack.webapi.SlackWebApiClient;

import com.slack.demoapp.ttt.db.IAppDBManager;
import com.slack.demoapp.ttt.exception.InternalError;
import com.slack.demoapp.ttt.model.Board;
import com.slack.demoapp.ttt.model.Game;

@Component(ShowProcessor.CONTEXT)
public class ShowProcessor implements IProcessor {

	public static final String CONTEXT  = "showProcessor";
	
	@Autowired
	private IAppDBManager dbManager;
	
	static Pattern showPattern = Pattern.compile("show$");
	
	@Override
	public void process(String channel, String cmd,
			SlackWebApiClient webApiClient, String userId, String username,
			HttpServletResponse response) throws SQLException, InternalError {
		
		Matcher m = showPattern.matcher(cmd);
		if (!m.find()) {
			showHelp(userId, webApiClient);
			return;
		}
		
		Game game = dbManager.fetchGameInChannel(channel);
		if (game == null) {
			webApiClient.postMessage(channel, "There is no game in progress in channel. You can start new game using /ttt challenge @user");
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		// TODO - Cache
		User nxtPlayer = webApiClient.getUserInfo(game.getNextPlayerId());
		Board board = new Board(game.getBoard());
		board.printBoard();
		webApiClient.postMessage(channel, board.printBoard() + "\n\nNext move by @" + nxtPlayer.getName());
		response.setStatus(HttpServletResponse.SC_OK);
		return;
	}

	@Override
	public void showHelp(String userId, SlackWebApiClient webApiClient) {
		String s = "Usage: /ttt show\n";
		String channelId = webApiClient.openDirectMessageChannel(userId);
		webApiClient.postMessage(channelId, s);
		webApiClient.closeDirectMessageChannel(channelId);
	}
	
	
}
