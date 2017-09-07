package com.slack.demoapp.servlet;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Data;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;

import com.slack.demoapp.ttt.TttApplication;
import com.slack.demoapp.ttt.cmdprocessor.ChallengeProcessor;
import com.slack.demoapp.ttt.cmdprocessor.MoveProcessor;
import com.slack.demoapp.ttt.cmdprocessor.ShowProcessor;
import com.slack.demoapp.ttt.db.IAppDBManager;
import com.slack.demoapp.ttt.db.SqlLiteDBManager;
import com.slack.demoapp.ttt.exception.InternalError;
import com.slack.demoapp.ttt.model.SlackApp;

/**
 * Jetty forwards /ttt to this class. It then delegates to a command processor.
 *
 */
@Data
@Lazy
@Import(SqlLiteDBManager.class)
@Component(TicTacToeCommandServlet.ID)
public class TicTacToeCommandServlet extends HttpServlet{

	public static final String ID = "WorkDocsCommandServlet";

	private static final long serialVersionUID = 3113514186497693193L;
	
	private IAppDBManager dbManager;
	
	private ChallengeProcessor challengeProcessor;
	
	private MoveProcessor moveProcessor;
	
	private ShowProcessor showProcessor;
	
	private SlackApp slackApp;
	
	static Pattern challengePattern = Pattern.compile("challenge(\\s+)<@(\\w+)\\|(\\w+)>");
	
	static Pattern movePattern = Pattern.compile("move(\\s+)([1-3]{1})(\\s+)([1-3]{1})");
	
	@PostConstruct
	public void init() {
		ApplicationContext context = TttApplication.getAppContext();
		dbManager = (SqlLiteDBManager) context
				.getBean(SqlLiteDBManager.CONTEXT);
		challengeProcessor = (ChallengeProcessor) context
				.getBean(ChallengeProcessor.CONTEXT);
		moveProcessor = (MoveProcessor) context.getBean(MoveProcessor.CONTEXT);
		showProcessor = (ShowProcessor) context.getBean(ShowProcessor.CONTEXT);
		slackApp = (SlackApp) context.getBean(SlackApp.CONTEXT);
	}
	
	private boolean isSlackCalling(String secretToken) {
		if (secretToken == null || !secretToken.equals(slackApp.getVerificationCode())) {
			return false;
		}
		return true;
	}
	
	private String retrieveUserAuthToken(String userId) throws InternalError {

		String token = dbManager.getAuthToken(userId);
		return token;
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		// validate coming from Slack
		if (!isSlackCalling(request.getParameter("token"))) {
			response.sendError(HttpStatus.BAD_REQUEST_400, "Invalid token!");
			return;
		}
		// Check team?
		
		// validate authenticated with App. If not, ask user to authorize.
		String token = null;
		try {
			token = retrieveUserAuthToken(request.getParameter("user_id"));
		} catch (InternalError e1) {
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
		if (token == null) {
			response.getWriter().write("Please authorize at "+ slackApp.getUrl());
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		
		String channel = request.getParameter("channel_id");
		String userId = request.getParameter("user_id");
		String username = request.getParameter("user_name");
		
		String cmd = request.getParameter("text");
		
		SlackWebApiClient webApiClient = SlackClientFactory
				.createWebApiClient(token);
		try {
			if (cmd.startsWith("challenge")) {
				challengeProcessor.process(channel, cmd, webApiClient, userId, username, response);
			} else if (cmd.startsWith("move")) {
				moveProcessor.process(channel, cmd, webApiClient, userId, username, response);
			} else if (cmd.startsWith("show")) {
				showProcessor.process(channel, cmd, webApiClient, userId, username, response);
			} else {
				showHelp(userId, webApiClient);
			}
		} catch (Exception e) {

		}
	}
	
	private void showHelp(String userId, SlackWebApiClient webApiClient) {
		String s = "Usage: /ttt [challenge|move|show]\n";
		String channelId = webApiClient.openDirectMessageChannel(userId);
		webApiClient.postMessage(channelId, s);
		webApiClient.closeDirectMessageChannel(channelId);
	}
}
