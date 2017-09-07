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

/**
 * Handles "challenge <@userid|username>"
 *
 */
@Component(ChallengeProcessor.CONTEXT)
public class ChallengeProcessor implements IProcessor {

	public static final String CONTEXT  = "challengeProcessor";
	
	@Autowired
	private IAppDBManager dbManager;
	
	static Pattern challengePattern = Pattern.compile("challenge(\\s+)<@(\\w+)\\|(\\w+)>");
	
	@Override
	public void process(String channel, String cmd,
			SlackWebApiClient webApiClient, String userId, String username,
			HttpServletResponse response) throws SQLException, InternalError {
		
		// TODO - Cache stuff
		if (dbManager.fetchGameInChannel(channel) != null) {
			webApiClient.postMessage(channel, "Another game is in progress in channel. You can see status of game using /ttt show");
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		
		Matcher m = challengePattern.matcher(cmd);
		if (m.find()) {
			String challengeeId = m.group(2);
			String challengeeUser = m.group(3);
			
			if (challengeeId.equals(userId)) {
				webApiClient.postMessage(channel, "C'mmon, play with someone other than yourself!");
				// TBD - Shall we give back 4xx code and not 200?
				response.setStatus(HttpServletResponse.SC_OK);
				return;
			}
			boolean isChallengeeInChannel = false;
			// Check challengee is present in our team?
			 for (String member : webApiClient.getChannelInfo(channel).getMembers()) {
				 if (member.equals(challengeeId)) {
					 isChallengeeInChannel = true;
					 break;
				 }
			 }
			if (!isChallengeeInChannel) {
				webApiClient.postMessage(channel,
						"C'mmon, play with someone in your channel!");
				// TBD - Shall we give back 4xx code and not 200?
				response.setStatus(HttpServletResponse.SC_OK);
				return;
			}
			
			dbManager.createNewGame(channel, userId, challengeeId);
			
			response.setStatus(HttpServletResponse.SC_OK);
			webApiClient.postMessage(channel, "New game started between @" + username + " and @" + challengeeUser);
			return;
		} else {
			showHelp(userId, webApiClient);
		}
		
	}
	
	
	@Override
	public void showHelp(String userId, SlackWebApiClient webApiClient) {
		String s = "Usage: /ttt challenge @username\n";
		String channelId = webApiClient.openDirectMessageChannel(userId);
		webApiClient.postMessage(channelId, s);
		webApiClient.closeDirectMessageChannel(channelId);
	}

}
