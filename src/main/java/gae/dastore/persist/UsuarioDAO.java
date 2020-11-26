package gae.dastore.persist;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

public class UsuarioDAO {

	Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public String guardar(User usuario) {
		datastore.put(objectToEntity(usuario));
		return usuario.getNombre();

	}

	public User obtenerUsuarioPorId(String id) {

		User usuario = null;

		Key key = datastore.newKeyFactory().setKind("User").newKey(id);
		Entity entity = datastore.get(key);

		if (entity != null) {
			usuario = (User) entityToKind(entity);
		}
		return usuario;
	}

//	public List<User> getAllUserFilter(String nombre) throws Exception {
//		List<User> acta;
//		try {
//			Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
//					.setFilter(CompositeFilter.and(PropertyFilter.eq("nombre", nombre))).build();
//
//			List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
//			acta = (List<Acta>) (List<?>) ClassConstructor.listaDeEntidades(results.iterator());
//		} catch (NoResultException ex) {
//			acta = null;
//		} catch (Exception ex) {
//			throw ex;
//		}
//		return acta;
//	}

	private static Object getObject(Object object, Entity entity) {

		while (object != null) {
			try {
				for (Field f : object.getClass().getDeclaredFields()) {
					if (!f.getName().contains("jdo")) {
						f.setAccessible(true);
						Object value = entity.getString(f.getName());
						String fieldType = f.getType().getSimpleName();
						f.set(object, value);
						if (fieldType.equals("Key")) {
							f.set(object, entity.getKey());
						}
					}
				}
				return object;
			} catch (Exception e) {
				e.printStackTrace();
				return object;
			}
		}
		return object;
	}

	private Object entityToKind(Entity entity) {

		Object objetoRetorno = null;

		try {
			// obtiene nombre de entidad para asignarselo a la clase
			objetoRetorno = getObject(Class.forName("gae.dastore.persist." + entity.getKey().getKind())
					.getDeclaredConstructor().newInstance(), entity);///
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return objetoRetorno;
	}

	private Entity objectToEntity(Object object) {
		Entity.Builder entityBuilder = null;
		Key key = null;

		try {
			String entityName = object.getClass().getSimpleName();
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field f : fields) {
				f.setAccessible(true);
				String fieldType = f.getType().getSimpleName();
				if (fieldType.equals("Key")) {
					key = datastore.newKeyFactory().setKind(entityName).newKey((String) f.get(object));
					entityBuilder = Entity.newBuilder(key);
					break;
				}
			}

			if (entityBuilder == null) {
				// Si el id no es de tipo Key, lo buscamos por la anotacion @ID de jpa
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
				if (keyVal != null) { // asigna llave de la entidad
					if (keyVal instanceof String) {
						key = datastore.newKeyFactory().setKind(entityName).newKey((String) keyVal);
						entityBuilder = Entity.newBuilder(key);
					} else if (keyVal instanceof Long) {
						key = datastore.newKeyFactory().setKind(entityName).newKey((Long) keyVal);
						entityBuilder = Entity.newBuilder(key);
					} else if (keyVal instanceof Integer) {
						key = datastore.newKeyFactory().setKind(entityName).newKey((Integer) keyVal);
						entityBuilder = Entity.newBuilder(key);
					}

				} else {
					throw new Exception("La entidad " + entityName + " no tiene Key");
				}
			}

			List<Field> campos = new ArrayList<Field>();
			for (Field f : fields) {
				f.setAccessible(true);
				String fieldName = f.getName();
				String fieldType = f.getType().getSimpleName();
				if (!fieldName.contains("jdo") && !fieldType.equals("Key")) {
					Object fieldValue = f.get(object);
					campos.add(f);
					entityBuilder.set(fieldName, fieldValue.toString()); // asigna campos a la entidad
				}
			}

			// setea clave primaria
		} catch (

		IllegalArgumentException ie) {
			System.err.println(ie);
		} catch (Exception e) {
			System.err.println(e);
		}
		return entityBuilder.build();
	}

}
