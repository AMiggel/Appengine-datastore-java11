package gae.dastore;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import gae.dastore.persist.User;
import gae.dastore.persist.UsuarioDAO;

@Path("/usuario")
public class UsuarioServlet {

	@Context
	private HttpServletRequest request;
	@Context
	private HttpServletResponse response;

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
