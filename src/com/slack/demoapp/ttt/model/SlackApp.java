package com.slack.demoapp.ttt.model;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slack.demoapp.ttt.db.IAppDBManager;
import com.slack.demoapp.ttt.exception.InternalError;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for a Slack app.
 *
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Component(SlackApp.CONTEXT)
public class SlackApp {
	
	public static final String CONTEXT = "slackApp";

	@Autowired
	private IAppDBManager dbManager;
	
	String appId;
	
	String appSecret;
	
	String verificationCode;
	
	String url;
	
	@PostConstruct
	void init() throws InternalError {
		SlackApp app = dbManager.getSlackApp();
		this.appId = app.appId;
		this.appSecret = app.appSecret;
		this.verificationCode = app.verificationCode;
		this.url = app.url;
	}
}
