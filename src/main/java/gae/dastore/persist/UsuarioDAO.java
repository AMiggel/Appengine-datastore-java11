package gae.dastore.persist;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

public class UsuarioDAO {

	Datastore datastore = DatastoreOptions.getDefaultInstance().getService();


	public Entity guardar(String nombre, String apellido) {
		
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();		
		KeyFactory usuarioKey = datastore.newKeyFactory().setKind("user");
		
		FullEntity entidad = Entity.newBuilder(usuarioKey.newKey())
				.set("nombre", nombre)
				.set("apellido", apellido)
				.build();
		
		return datastore.put(entidad);
		
	}

}
