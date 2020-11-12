package pe.edu.upc.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pe.edu.upc.entity.Usuario;
import pe.edu.upc.iservice.IUsuarioService;

@Controller
@RequestMapping("/")
public class HomeController {
	@Autowired
	private IUsuarioService uS;
	
	@GetMapping("index")
	public String index() { return "index";	}
	
	@GetMapping("login") 
	public String login() { return "login"; }
	
	@GetMapping("denied")
	public String denied(Model model, Principal principal) {
		if(principal == null) {
			return "/login";
		}
		Usuario user = uS.findByUname(principal.getName()).get();
		
		model.addAttribute("admin", user.isIsadmin());
		
		return "/denied";
	}
}
