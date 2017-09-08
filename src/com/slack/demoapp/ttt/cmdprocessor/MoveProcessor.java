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

@Component(MoveProcessor.CONTEXT)
public class MoveProcessor implements IProcessor {

	public static final String CONTEXT = "moveProcessor";

	@Autowired
	private IAppDBManager dbManager;

	static Pattern movePattern = Pattern
			.compile("move(\\s+)([1-3]{1})(\\s+)([1-3]{1})");

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

		// Check user is entitiled to move
		if (!game.getNextPlayerId().equals(userId)) {
			webApiClient
					.postMessage(channel,
							"It's not @"+ username  +" move! You can see the status new game using /ttt show");
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		Board board = new Board(game.getBoard());
		String nxtPlayer;
		char move;
		if (game.getNextPlayerId().equals(game.getChallengerId())) {
			nxtPlayer = game.getChallengeeId();
			move = 'O';
		} else {
			nxtPlayer = game.getChallengerId();
			move = 'X';
		}

		Matcher m = movePattern.matcher(cmd);
		if (m.find()) {
			int row = Integer.parseInt(m.group(2));
			int col = Integer.parseInt(m.group(4));

			char ch = board.get(row - 1, col - 1);
			if (ch != '-') {
				webApiClient
						.postMessage(channel,
								"It's not a valid move! You can see the status new game using /ttt show");
				response.setStatus(HttpServletResponse.SC_OK);
				return;
			}

			board.set(move, row - 1, col - 1);
			dbManager.updateGame(channel, board.toString(), nxtPlayer);

			boolean gameOver = processIfGameOver(channel, board, move, row - 1,
					col - 1, webApiClient, username, response);
			String nxtMoveStr = "";
			if (!gameOver) {
				User nxtPlayerUser = webApiClient.getUserInfo(nxtPlayer);
				nxtMoveStr = "\n\nNext move by @" + nxtPlayerUser.getName();
			}
			// Print
			webApiClient.postMessage(channel, board.printBoard() + nxtMoveStr);
			response.setStatus(HttpServletResponse.SC_OK);

			return;
		} else {
			showHelp(userId, webApiClient);
			return;
		}
	}

	private boolean processIfGameOver(String channel, Board board, char ch,
			int r, int c, SlackWebApiClient webApiClient, String username,
			HttpServletResponse response) throws InternalError {

		if (board.hasWon(ch, r, c)) {
			webApiClient.postMessage(channel, "Congrats on winning @"
					+ username);
			response.setStatus(HttpServletResponse.SC_OK);
			dbManager.deleteGame(channel);
			return true;
		} else if (board.isDraw()) {
			webApiClient.postMessage(channel, "No one won, start a new game!");
			response.setStatus(HttpServletResponse.SC_OK);
			dbManager.deleteGame(channel);
			return true;
		}
		return false;
	}

	@Override
	public void showHelp(String userId, SlackWebApiClient webApiClient) {
		String s = "Usage: /ttt move [1-3] [1-3]\n";
		String channelId = webApiClient.openDirectMessageChannel(userId);
		webApiClient.postMessage(channelId, s);
		webApiClient.closeDirectMessageChannel(channelId);
	}

}
