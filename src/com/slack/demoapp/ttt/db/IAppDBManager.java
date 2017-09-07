package com.slack.demoapp.ttt.db;

import com.slack.demoapp.ttt.exception.InternalError;
import com.slack.demoapp.ttt.model.Game;
import com.slack.demoapp.ttt.model.SlackApp;
/**
 * 
 * Interface for metadata management.
 * 
 */

public interface IAppDBManager {

	public static final String CONTEXT = "AppDBManager";
	
	public String getAuthToken(String user_id) throws InternalError;
		
	public void insertAuthToken(String user_id, String token)
			throws InternalError;
	
	public Game fetchGameInChannel(String channel_id) throws InternalError ;
		
	public void updateGame(String channel_id, String board, String nextPerson) throws InternalError;

	public boolean createNewGame(String channel_id, String challenger,
			String challengee) throws InternalError;
	
	public void deleteGame(String channel) throws InternalError;
	
	public SlackApp getSlackApp() throws InternalError;
	
}
