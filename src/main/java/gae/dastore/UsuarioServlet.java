package gae.dastore;

import java.io.IOException;


import gae.dastore.persist.User;
import gae.dastore.persist.UsuarioDAO;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationPath("api")
public class UsuarioServlet extends Application{


	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+ ";charset=utf-8")
	public Response doPost(User user) throws IOException {

		UsuarioDAO usuario = new UsuarioDAO();
		
		usuario.guardar(user);
		return Response.ok("200",MediaType.APPLICATION_JSON).build();
	}

	
//	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		UsuarioDAO usuario = new UsuarioDAO();
//
//		usuario.eliminar(request.getParameter("id"));
//		response.setContentType("text/html");
//		response.getWriter().println("<h1>user deleted!</h1>");
//	}
//
//	@Override
//	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		UsuarioDAO usuario = new UsuarioDAO();
//
//		response.setContentType("application/json");
//		PrintWriter out = response.getWriter();
//		out.print(usuario.obtenerUsuarios().toString());
//		out.flush();
//
//	}
}
