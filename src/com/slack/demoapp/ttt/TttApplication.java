package com.slack.demoapp.ttt;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The main application class.
 * 
 * It loads the spring context and then starts the {@link AppServer} to accept requests.
 */
public class TttApplication {

	static ApplicationContext context;
	public static void main(String[] args) throws Exception {
		context = new ClassPathXmlApplicationContext(
				"beans.xml");

		AppServer runner = (AppServer) context
				.getBean(AppServer.class);

		runner.run();
		((ClassPathXmlApplicationContext) context).close();
	}
	
	public static ApplicationContext getAppContext() {
		return context;
	}
}
