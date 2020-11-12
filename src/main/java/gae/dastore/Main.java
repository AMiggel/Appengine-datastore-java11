package gae.dastore;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration.ClassList;
import org.eclipse.jetty.webapp.WebAppContext;

public class Main {

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
		      System.err.println("Usage: need a relative path to the war file to execute");
		      System.exit(1);
		    }
		    System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StrErrLog");
		    System.setProperty("org.eclipse.jetty.LEVEL", "INFO");

		    // Create a basic Jetty server object that will listen on port defined by
		    // the PORT environment variable when present, otherwise on 8080.
		    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
		    Server server = new Server(port);

		    // The WebAppContext is the interface to provide configuration for a web
		    // application. In this example, the context path is being set to "/" so
		    // it is suitable for serving root context requests.
		    WebAppContext webapp = new WebAppContext();
		    webapp.setContextPath("/");
		    webapp.setWar(args[0]);
		    ClassList classlist = ClassList.setServerDefault(server);

		    // Enable Annotation Scanning.
		    classlist.addBefore(
		        "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
		        "org.eclipse.jetty.annotations.AnnotationConfiguration");

		    // Set the the WebAppContext as the ContextHandler for the server.
		    server.setHandler(webapp);

		    // Start the server! By using the server.join() the server thread will
		    // join with the current thread. See
		    // "http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()"
		    // for more details.
		    server.start();
		    server.join();
		  }

}
