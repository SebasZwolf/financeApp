package pe.edu.upc;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import pe.edu.upc.entity.Usuario;
import pe.edu.upc.iservice.IUsuarioService;

public class MyAuthSuccHadler implements AuthenticationSuccessHandler{

	@Autowired
	private IUsuarioService uS;
	
	@Bean
	public PasswordEncoder pEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	protected Log logger = LogFactory.getLog(this.getClass());
	
	private RedirectStrategy rS = new DefaultRedirectStrategy();
		
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		System.out.println("authenticated");
		
		handle(request, response, authentication);
        clearAuthenticationAttributes(request);
	}
	
	protected void handle(
	        HttpServletRequest request,
	        HttpServletResponse response, 
	        Authentication authentication
	) throws IOException {
	 
	    String targetUrl = determineTargetUrl(authentication);
	 
	    if (response.isCommitted()) {
	        logger.debug(
	                "Response has already been committed. Unable to redirect to "
	                        + targetUrl);
	        return;
	    }
	 
	    rS.sendRedirect(request, response, targetUrl);
	}
	
	protected String determineTargetUrl(final Authentication authentication) {
		 
		final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
	    
		boolean safe = false;
		for (final GrantedAuthority grantedAuthority : authorities) {
	        String aname = grantedAuthority.getAuthority();
	        
	        if(aname == "ROLE_USER")
	        	safe = true;
	        
	        if(aname == "ROLE_ADMIN")
	        	return "/admin/home";
	    }
		
	    if(safe) {
	    	PasswordEncoder pE = pEncoder();
	    	
	    	String upass = uS.findByUname(authentication.getName()).get().getUpass();
	    	
	    	if(pE.matches("claro2018", upass)) {
	    		Usuario user = uS.findByUname(authentication.getName()).get();
	    		int id = user.getId();
	    		
	    		
	    		return "/client/" + String.valueOf(id)+ "/activate";
	    	}
	    	return "/client/home";
	    }
	
	    throw new IllegalStateException();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request) {
	    HttpSession session = request.getSession(false);
	    if (session == null) {
	        return;
	    }
	    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}
}
