package com.slack.demoapp.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.context.ApplicationContext;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Authentication;
import allbegray.slack.type.OAuthAccessToken;
import allbegray.slack.webapi.SlackWebApiClient;

import com.slack.demoapp.ttt.TttApplication;
import com.slack.demoapp.ttt.db.SqlLiteDBManager;
import com.slack.demoapp.ttt.model.SlackApp;

/**
 * Jetty forwards Oauth challenges from Slack to this class.
 *
 */
public class SlackAuthServlet extends HttpServlet {

	private static final Logger LOGGER = Logger
			.getLogger(SlackAuthServlet.class.getName());

	private static final long serialVersionUID = 7904589078245689560L;

	private SqlLiteDBManager dbManager;
	
	private SlackApp slackApp;

	@PostConstruct
	public void init() {

		ApplicationContext context = TttApplication.getAppContext();

		dbManager = (SqlLiteDBManager) context
				.getBean(SqlLiteDBManager.CONTEXT);
		slackApp = (SlackApp) context.getBean(SlackApp.CONTEXT);
	}

	String getSlackToken(String code) throws Exception {

		try {
			SlackWebApiClient webApiClient = SlackClientFactory
					.createWebApiClient(null);
			OAuthAccessToken accessToken = webApiClient.accessOAuth(
					slackApp.getAppId(), slackApp.getAppSecret(),
					code, null);
			SlackWebApiClient webApiClient2 = SlackClientFactory
					.createWebApiClient(accessToken.getAccess_token());
			Authentication auth = webApiClient2.auth();
			dbManager.insertAuthToken(auth.getUser_id(),
					accessToken.getAccess_token());
			return auth.getUrl();
		} catch (Exception e) {
			LOGGER.info("Could not procure token " + e);
		}
		return null;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String code = request.getParameter("code");
		if (code == null) {
			response.sendError(HttpStatus.BAD_REQUEST_400);
			return;
		}

		try {
			String teamUrl = getSlackToken(code);
			response.setStatus(HttpServletResponse.SC_OK);
			// All went well, lets go back to team!
			response.sendRedirect(response.encodeRedirectURL(teamUrl));
		} catch (Exception e) {
			response.sendError(500, "Could not complete Oauth flow!");
		}
	}
}
