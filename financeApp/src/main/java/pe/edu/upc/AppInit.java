package pe.edu.upc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pe.edu.upc.entity.Usuario;
import pe.edu.upc.repository.UsuarioRepository;

@Service
public class AppInit implements CommandLineRunner{

	@Autowired
	private UsuarioRepository uR;
	
	@Bean
	private PasswordEncoder pE() {
		return new BCryptPasswordEncoder();
	}
		
	@Override
	public void run(String... args) throws Exception {
		
		if(uR.findByIsadmin(true).size() > 0) return;

		Usuario entity = new Usuario();
				
		entity.setUname("admin");
		entity.setUpass( pE().encode("admin"));
		entity.setIsadmin(true);
		entity.setIsenabled(true);
		entity.setClient(null);
		
		System.out.println(entity.getUpass());
		
		uR.save(entity);
	}

}
