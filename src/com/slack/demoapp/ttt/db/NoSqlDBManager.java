package com.slack.demoapp.ttt.db;

import com.slack.demoapp.ttt.exception.InternalError;
import com.slack.demoapp.ttt.model.Game;
import com.slack.demoapp.ttt.model.SlackApp;

/**
 * Impl for Nosql like DynamoDB. 
 * 
 * Depending on team size we can switch the data store. All references use {@link IAppDBManager} so
 * the plugin model will work.
 * 
 * This is future work!
 * 
 */
public class NoSqlDBManager implements IAppDBManager {

	@Override
	public String getAuthToken(String user_id) throws InternalError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertAuthToken(String user_id, String token)
			throws InternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Game fetchGameInChannel(String channel_id) throws InternalError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateGame(String channel_id, String board, String nextPerson)
			throws InternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean createNewGame(String channel_id, String challenger,
			String challengee) throws InternalError {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteGame(String channel) throws InternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SlackApp getSlackApp() throws InternalError {
		// TODO Auto-generated method stub
		return null;
	}

}
