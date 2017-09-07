package com.slack.demoapp.ttt.cmdprocessor;

import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import allbegray.slack.webapi.SlackWebApiClient;

import com.slack.demoapp.ttt.exception.InternalError;

public interface IProcessor {

	void process(String channel, String cmd,
			SlackWebApiClient webApiClient, String userId, String username,
			HttpServletResponse response) throws SQLException, InternalError;
	
	void showHelp(String userId, SlackWebApiClient webApiClient);
}
