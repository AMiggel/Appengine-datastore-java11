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

		int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
		Server server = new Server(port);

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar(args[0]);
		ClassList classlist = ClassList.setServerDefault(server);

		classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
				"org.eclipse.jetty.annotations.AnnotationConfiguration");

		server.setHandler(webapp);
		server.start();
		server.join();
	}

}
