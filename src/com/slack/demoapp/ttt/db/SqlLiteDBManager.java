package com.slack.demoapp.ttt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.slack.demoapp.ttt.exception.InternalError;
import com.slack.demoapp.ttt.model.Game;
import com.slack.demoapp.ttt.model.SlackApp;

@Component(IAppDBManager.CONTEXT)
public class SqlLiteDBManager implements IAppDBManager {

	private static final Logger LOGGER = Logger.getLogger(SqlLiteDBManager.class.getName());
	
	private static final String JDBC_CONN = "jdbc:sqlite:ttt.db";
	
	// TODO - Connection pools
	
	@PreDestroy
	public void cleanUp() throws Exception {
		
	}

	static Connection getConnection()
			throws SQLException {
		return DriverManager.getConnection(JDBC_CONN);
	}
	
	@PostConstruct
	void init() throws com.slack.demoapp.ttt.exception.InternalError {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new com.slack.demoapp.ttt.exception.InternalError("Could not load org.sqlite.JDBC");
		}
		
		Connection conn = null;
		try {
			conn = getConnection();
			conn.createStatement()
					.execute(
							"CREATE TABLE IF NOT EXISTS ongoing_challenges (channel PRIMARY KEY, challenger, challengee, board, personToMove) ");
			conn.createStatement()
					.execute(
							"CREATE TABLE IF NOT EXISTS tokens (user PRIMARY KEY, token) ");
			conn.createStatement()
			.execute(
					"CREATE TABLE IF NOT EXISTS AppStore (AppId PRIMARY KEY, Secret, VerificationCode, url) ");

		} catch (SQLException e) {
			throw new com.slack.demoapp.ttt.exception.InternalError(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.info("Could not clean up " + e);
				} 
			}
		}

	}

	public SlackApp getSlackApp() throws InternalError {
		
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = getConnection();
			Statement stat = conn.createStatement();
			String cmd = "select * from AppStore;";
			
			rs = stat.executeQuery(cmd);
			String secret = null;
			String verificationCode = null;
			String url = null;
			String appId = null;
			while (rs.next()) {
				appId = rs.getString("AppId");
				secret = rs.getString("Secret");
				verificationCode = rs.getString("VerificationCode");
				url = rs.getString("url");
			}
			return SlackApp.builder().appId(appId)
					.appSecret(secret).verificationCode(verificationCode)
					.url(url).build();
		} catch (SQLException e) {
			throw new com.slack.demoapp.ttt.exception.InternalError(e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e)  {
				LOGGER.info("Could not clean up " + e);
			} 
		}
	}
	
	public String getAuthToken(String user_id) throws com.slack.demoapp.ttt.exception.InternalError {
		String token = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = getConnection();
			Statement stat = conn.createStatement();
			String cmd = "select * from tokens where user='" + user_id + "';";
			System.out.println(cmd);
			rs = stat.executeQuery(cmd);
			while (rs.next()) {
				token = rs.getString("token");
			}
			return token;
		} catch (SQLException e) {
			throw new com.slack.demoapp.ttt.exception.InternalError(e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e)  {
				LOGGER.info("Could not clean up " + e);
			} 
		}
	}

	public void insertAuthToken(String user_id, String token)
			throws  com.slack.demoapp.ttt.exception.InternalError {

		Connection conn = null;
		PreparedStatement prep = null;
		try {
			conn = getConnection();

			prep = conn
					.prepareStatement("INSERT OR REPLACE into tokens values (?, ?);");

			prep.setString(1, user_id);
			prep.setString(2, token);
			prep.addBatch();
			// Start a transaction
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new com.slack.demoapp.ttt.exception.InternalError(e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (prep != null) {
					prep.close();
				}
			} catch (SQLException e)  {
				LOGGER.info("Could not clean up " + e);
			} 
		}
	}

	public Game fetchGameInChannel(String channel_id) throws com.slack.demoapp.ttt.exception.InternalError {
		Statement stat = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = getConnection(); 
			stat = conn.createStatement();
			rs = stat
					.executeQuery("select * from ongoing_challenges where channel='"
							+ channel_id + "';");
			Game game = null;
			while (rs.next()) {
				game = new Game();
				game.setChanneld(rs.getString("channel"));
				game.setChallengerId(rs.getString("challenger"));
				game.setChallengeeId(rs.getString("challengee"));
				game.setNextPlayerId(rs.getString("personToMove"));
				game.setBoard(rs.getString("board"));
			}
			return game;
		} catch (SQLException e) {
			throw new com.slack.demoapp.ttt.exception.InternalError(e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e)  {
				LOGGER.info("Could not clean up " + e);
			} 
		}
	}

	public void updateGame(String channel_id, String board, String nextPerson) throws com.slack.demoapp.ttt.exception.InternalError {
		String sql = "UPDATE ongoing_challenges SET board = ? , "
				+ "personToMove = ? " + "WHERE channel = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);

			// set the corresponding param
			pstmt.setString(1, board);
			pstmt.setString(2, nextPerson);
			pstmt.setString(3, channel_id);
			// update
			conn.setAutoCommit(false);
			pstmt.executeUpdate();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new com.slack.demoapp.ttt.exception.InternalError(e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e)  {
				LOGGER.info("Could not clean up " + e);
			} 
		}
	}
	
	public boolean createNewGame(String channel_id, String challenger,
			String challengee) throws com.slack.demoapp.ttt.exception.InternalError {

		PreparedStatement prep = null;
		Connection conn = null;
		
		try {
			conn = getConnection();
			prep = conn
					.prepareStatement("insert into ongoing_challenges values(?, ?, ?,?,?)");
			prep.setString(1, channel_id);
			prep.setString(2, challenger);
			prep.setString(3, challengee);
			prep.setString(4, "---------");
			prep.setString(5, challenger);
			conn.setAutoCommit(false);
			prep.execute();
			conn.setAutoCommit(true);
			return true;
		} catch (SQLException e) {
			throw new com.slack.demoapp.ttt.exception.InternalError(e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (prep != null) {
					prep.close();
				}
			} catch (SQLException e)  {
				LOGGER.info("Could not clean up " + e);
			} 
		}
	}
	
	public void deleteGame(String channel) throws InternalError {
		String sql = "DELETE from ongoing_challenges WHERE channel = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, channel);
			conn.setAutoCommit(false);
			pstmt.executeUpdate();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new com.slack.demoapp.ttt.exception.InternalError(e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e)  {
				LOGGER.info("Could not clean up " + e);
			} 
		}
	}
}
