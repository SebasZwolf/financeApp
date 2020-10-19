package pe.edu.upc.service;

import org.springframework.stereotype.Service;

import pe.edu.upc.entity.Usuario;
import pe.edu.upc.entity.UsuarioPrincipal;
import pe.edu.upc.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class JpaUserDetailService implements UserDetailsService {

	@Autowired
	private UsuarioRepository uR;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario cuser = uR.findByUname(username).get();
		return new UsuarioPrincipal(cuser);
	}

}
