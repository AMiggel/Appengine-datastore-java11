package gae.dastore.persist;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

public class UsuarioDAO {

	public String guardar(User usuario) {
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		datastore.put(objectToEntity(usuario));
		return usuario.getNombre();

	}

	public Entity obtenerUsuarioPorNombre(String nombre) {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		User usuario = null;

		Key key = datastore.newKeyFactory().setKind(usuario.getClass().getName()).newKey(nombre);

		Entity entity = datastore.get(key);

		return entity;
	}

	public static Object getObject(Object object, Entity entity) {
		while (object != null) {
			try {
				for (Field f : object.getClass().getDeclaredFields()) {
					if (!f.getName().contains("jdo")) {
						f.setAccessible(true);
						try {
							// Claves primarias
							Object value;
							if ("stCodigo".equalsIgnoreCase(f.getName())
									|| "stNumeroSolicitud".equalsIgnoreCase(f.getName())
									|| "stId".equalsIgnoreCase(f.getName())
									|| "stEmail".equalsIgnoreCase(f.getName())) {
								if (!(object instanceof User) && "stEmail".equalsIgnoreCase(f.getName())) {
									value = entity.getString(f.getName());
								} else {
									value = entity.getKey().getName();
								}
							} else {
								value = entity.getString(f.getName());
							}

							String fieldType = f.getType().getSimpleName();
							// System.out.println("Campo "+f.getName()+" Valor"+value);
							if (fieldType.equals("Integer") || fieldType.equals("int")) {
								value = Integer.parseInt(value.toString());
							} else if (fieldType.equals("Long") || fieldType.equals("long")) {
								value = Long.parseLong(value.toString());
							}
							f.set(object, value);

							// Seteamos la clave
							if (fieldType.equals("Key")) {
								f.set(object, entity.getKey());
							}
						} catch (IllegalArgumentException ie) {
							ie.printStackTrace();
						} catch (IllegalAccessException ae) {
							ae.printStackTrace();
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

	public Entity objectToEntity(Object object) {
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
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
