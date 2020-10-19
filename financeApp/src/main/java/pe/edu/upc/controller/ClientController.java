package pe.edu.upc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.upc.entity.Usuario;
import pe.edu.upc.iservice.IUsuarioService;

@Controller
@RequestMapping("/client")
public class ClientController {

	@Autowired
	private IUsuarioService uS;
	
	@GetMapping("/home")
	public String goHome(Model model, Authentication auth) {
		Usuario user = uS.findByUname(auth.getName()).get();
		
		System.out.println(user.getUname());
		
		model.addAttribute("cliente", user.getClient());
		
		return "/client/home";
	}

	@GetMapping("/{id}/activate")
	public String goActivate(Model model, @PathVariable(name="id") int id) {
		
		model.addAttribute("id", id);
		
		return "/client/activate";
	}
	
	@PostMapping("/{id}/activate")
	public String Activate(Model model, @PathVariable(name="id")int id, @RequestParam(name="password") String pass) {
		
		Usuario user = uS.findById(id).get();
		user.setUpass(new BCryptPasswordEncoder().encode(pass));
		uS.insert(user);
		
		return "redirect:/client/home";
	}
}
