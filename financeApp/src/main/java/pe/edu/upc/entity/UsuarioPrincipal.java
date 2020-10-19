package pe.edu.upc.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UsuarioPrincipal implements UserDetails{
	private Usuario cuser;
	public UsuarioPrincipal(Usuario user) {
		this.cuser = user;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List <GrantedAuthority> authorities  = new ArrayList<>();
		
		if(cuser.isIsadmin()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}else {
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
		 
		return authorities;
	}
	@Override
	public String getPassword() {
		return cuser.getUpass();
	}
	@Override
	public String getUsername() {
		return cuser.getUname();
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return cuser.isIsenabled();
	}

}
