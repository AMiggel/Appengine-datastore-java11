package gae.dastore.persist;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Value;

public class UsuarioDAO {

	public String guardar(User usuario) {
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		datastore.put(objectToEntity(usuario));
		return usuario.getNombre();

	}

	public void eliminar(String id) {
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		Key usuarioKey = asignarKey(id);

		datastore.delete(usuarioKey);
	}

//	public List<User> obtenerUsuarios() {
//		List<User> usuarios = new ArrayList<User>();
//		Query<Entity> query = Query.newEntityQueryBuilder().setKind("user").build();
//		QueryResults<Entity> results = datastore.run(query);
//		while (results.hasNext()) {
//			User usuario = new User();
//			Entity entity = results.next();
//			usuario.setNombre(entity.getString("nombre"));
//			usuario.setApellido(entity.getString("apellido"));
//			usuario.setId(entity.getString("id"));
//			usuarios.add(usuario);
//		}
//		return usuarios;
//	}

	private Key asignarKey(String value) {
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		Key key = datastore.newKeyFactory().setKind("user").newKey(value);

		return key;
	}

	public Entity objectToEntity(Object object) {
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		Entity entity = null;
		try {
			String entityName = object.getClass().getSimpleName();
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field f : fields) {
				f.setAccessible(true);
				String fieldType = f.getType().getSimpleName();
				if (fieldType.equals("Key")) {
					Key key = datastore.newKeyFactory().setKind(entityName).newKey((String) f.get(object));
					entity = Entity.newBuilder(key).build();
					break;
				}
			}

			if (entity == null) {
				// Si el id no es de tipo Key, lo buscamos
				Object keyVal = null;
				outerloop: for (Field f : fields) {
					f.setAccessible(true);
					Annotation[] anotaciones = f.getDeclaredAnnotations();
					if (anotaciones == null)
						continue;
					for (Annotation annotation : anotaciones) {
						String nombreAnotacion = annotation.annotationType().getSimpleName();
						if (nombreAnotacion.equals("Id")) {
							keyVal = f.get(object);
							break outerloop;
						}
					}
				}
				if (keyVal != null) {
					if (keyVal instanceof String) {
						Key key = datastore.newKeyFactory().setKind(entityName).newKey((String) keyVal);
						entity = Entity.newBuilder(key).build();
					}
//					} else if (keyVal instanceof Long) {
//						Key key = datastore.newKeyFactory().setKind(entityName).newKey((Long) keyVal);
//						entity = Entity.newBuilder(key).build();
//					} else if (keyVal instanceof Integer) {
//						Key key = datastore.newKeyFactory().setKind(entityName).newKey((Integer) keyVal);
//						entity = Entity.newBuilder(key).build();
//					}
				} else {
					throw new Exception("La entidad " + entityName + " no tiene Key");
				}
			}
			for (Field f : fields) {
				f.setAccessible(true);
				String fieldName = f.getName();
				String fieldType = f.getType().getSimpleName();
				if (!fieldName.contains("jdo") && !fieldType.equals("Key")) {
					Object fieldValue = f.get(object);
					entity.newBuilder().set(fieldName, (Value<?>) fieldValue);
				}
			}
			// setea clave primaria
		} catch (

		IllegalArgumentException ie) {
			System.err.println(ie);
		} catch (Exception e) {
			System.err.println(e);
		}
		return entity;
	}

}
