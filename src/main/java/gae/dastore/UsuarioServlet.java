package gae.dastore;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import gae.dastore.persist.User;
import gae.dastore.persist.UsuarioDAO;
import gae.dastore.pojo.UserPojo;

@Path("/usuario")
public class UsuarioServlet {

	private static final Logger log = Logger.getLogger(UsuarioServlet.class.getName());

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doPost(User user) throws IOException {
		log.setLevel(Level.INFO);

		UsuarioDAO usuario = new UsuarioDAO();

		usuario.guardar(user);
		return Response.ok("200", MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public UserPojo getUser(@PathParam("id") String id) {

		UserPojo userPojo = new UserPojo();
		UsuarioDAO usuario = new UsuarioDAO();

		User user = usuario.obtenerUsuarioPorId(id);

		userPojo.setApellido(user.getApellido());
		userPojo.setNombre(user.getNombre());
		userPojo.setId(user.getId());

		return userPojo;

	}
}
