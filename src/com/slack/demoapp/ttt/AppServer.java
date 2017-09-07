package com.slack.demoapp.ttt;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.stereotype.Component;

import com.slack.demoapp.servlet.SlackAuthServlet;
import com.slack.demoapp.servlet.TicTacToeCommandServlet;

/**
 * Starts a {@link Jetty} webserver that accepts requests and forwards to servlets.
 *
 */
@Component
public class AppServer {

	public void run() throws Exception {

		// Create a basic jetty server object that will listen on port 8080.
		// Note that if you set this to port 0 then a randomly available port
		// will be assigned that you can either look in the logs for the port,
		// or programmatically obtain it for use in test cases.
		Server server = new Server(8080);

		ServletContextHandler ctx = new ServletContextHandler();
		ctx.setContextPath("/");

		DefaultServlet defaultServlet = new DefaultServlet();
		ServletHolder holderPwd = new ServletHolder("default", defaultServlet);
		 
		// Raw servlet
		ctx.addServlet(holderPwd, "/*");
		// Our servlets
		ctx.addServlet(TicTacToeCommandServlet.class, "/ttt");
		ctx.addServlet(SlackAuthServlet.class, "/authorization");
		
		server.setHandler(ctx);
		
		// Start things up!
		server.start();

		server.join();
	}
}
