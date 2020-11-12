package gae.dastore;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gae.dastore.persist.UsuarioDAO;


@WebServlet(name = "UsuarioServlet", value = "/usuario")
public class UsuarioServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		UsuarioDAO usuario = new UsuarioDAO();		
		usuario.guardar(request.getParameter("nombre"), request.getParameter("apellido"));
		
		
		response.setContentType("text/html");
		response.getWriter().println("<h1>user created!</h1>");
	}
}
