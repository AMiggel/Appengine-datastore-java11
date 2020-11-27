package gae.dastore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
	public Response crearUsuario(User user) throws IOException {
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/fecha")
	public List<UserPojo> obtenerUsuariosPorFecha(@QueryParam("fechaCreacion") String fechaCreacion) throws Exception {

		List<UserPojo> userPojo = null;
		UsuarioDAO usuario = new UsuarioDAO();

		List<User> users = usuario.obtenerUsuarioPorFecha(fechaCreacion);

		if (users != null) {
			userPojo = prepararPojoFilter(users);
		}
		return userPojo;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserPojo> getUserByName(@QueryParam("nombre") String nombre) throws Exception {

		List<UserPojo> userPojo = null;
		UsuarioDAO usuario = new UsuarioDAO();

		List<User> users = usuario.obtenerUsuarioPorNombre(nombre);

		if (users != null) {
			userPojo = prepararPojoFilter(users);
		}
		return userPojo;
	}

	private List<UserPojo> prepararPojoFilter(List<User> usuarios) throws Exception {
		log.setLevel(Level.INFO);
		List<UserPojo> usuariosPojos = new ArrayList<UserPojo>();
		Iterator<User> itrUser = usuarios.iterator();
		UserPojo userPojo = null;

		while (itrUser.hasNext()) {
			User user = (User) itrUser.next();
			userPojo = new UserPojo();
			userPojo.setApellido(user.getApellido());
			userPojo.setNombre(user.getNombre());
			userPojo.setId(user.getId());

			usuariosPojos.add(userPojo);
		}
		return usuariosPojos;
	}
}
