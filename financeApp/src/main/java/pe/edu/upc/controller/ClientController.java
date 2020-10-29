package pe.edu.upc.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.upc.entity.Cuenta;
import pe.edu.upc.entity.Move;
import pe.edu.upc.entity.Usuario;
import pe.edu.upc.iservice.ICuentaService;
import pe.edu.upc.iservice.IMoveService;
import pe.edu.upc.iservice.IUsuarioService;

@Controller
@RequestMapping("/client")
public class ClientController {

	@Autowired
	private IUsuarioService uS;
	
	@Autowired
	private ICuentaService cS;
	
	@Autowired
	private IMoveService mS;
	
	@GetMapping("/home")
	public String goHome(Model model, Authentication auth) {
		Usuario user = uS.findByUname(auth.getName()).get();
		
		System.out.println(user.getUname());
		
		model.addAttribute("cliente", user.getClient());
		
		return "/client/home";
	}

	@GetMapping("/account/{account_id}/detail")
	public String goAccountDetail(Model model, Authentication auth, @PathVariable(name="account_id") int account_id) {
		Cuenta cuenta =  cS.findById(account_id).get();
		
		if( !auth.getName().equals(cuenta.getOwner().getAccount().getUname()) ) {
			System.out.println( auth.getName() + " " + cuenta.getOwner().getAccount().getUname());
			
			return "redirect:/client/home";
		}
		
		model.addAttribute("cuenta", cuenta);
		
		model.addAttribute("moveList", mS.list(cuenta));
		
		return "/client/account/detail";
	}
	
	@GetMapping("/{id}/activate")
	public String goActivate(Model model, @PathVariable(name="id") int id) {
		
		model.addAttribute("id", id);
		
		return "/client/activate";
	}
	
	@GetMapping("/account/{account_id}/move/new")
	public String goMakeMove(Model model, Authentication auth, @PathVariable(name="account_id") int account_id) {
		
		
		
		model.addAttribute("account_id", account_id);
		model.addAttribute("move", new Move());
		
		return "/client/account/move/new";
	}
	
	@GetMapping("/account/{account_id}/move/detail")
	public String goSeePayments(Model model, Authentication auth, @PathVariable(name="account_id") int account_id) {
		
		List<String[]> pays = new ArrayList<>();
		
		/*Cuenta cuenta = cS.findById(account_id).get();
		
		List<Move> moves = mS.list(cuenta);
		List<Move> auxmoves = new ArrayList<>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(cuenta.getEnd());
		int lenght = calendar.get(Calendar.YEAR) * 12 +  calendar.get(Calendar.MONTH);
		calendar.setTime(cuenta.getStart());
		lenght = lenght - calendar.get(Calendar.YEAR)*12 - calendar.get(Calendar.MONTH);
		
		moves.forEach(p->{
			
		});	*/
		
		
		String[] e = {
			"1","12-10-2020","1200"	
		};
		pays.add(e);
		
		model.addAttribute("listPayments", pays);
		return "client/account/move/detail";
	}
	
	@PostMapping("/account/{account_id}/move/new")
	public String MakeMove(@Valid Move move, BindingResult result,  Model model, Authentication auth, @PathVariable(name="account_id") int account_id) {
		if(result.hasErrors()) {
			model.addAttribute("account_id", account_id);

			model.addAttribute("move", move);
			
			return "/client/account/move/new";
		}
		
		move.setAccount(cS.findById(account_id).get());
		Cuenta cuenta = move.getAccount();
		cuenta.setBalance((float) (cuenta.getBalance() + move.getValue()));
		cS.insert(cuenta);
		
		Optional<Move> auxMmove = mS.getLast(cS.findById(account_id).get());

		if(auxMmove.isPresent())
		{
			Move auxmove = auxMmove.get();
		
			if(auxmove.getCommit_date().compareTo(move.getCommit_date()) == 0) {
				move.setId(auxmove.getId());	
				move.setValue(move.getValue() + auxmove.getValue());
			}
		}
		
		mS.insert(move);
		
		return "redirect:/client/account/" + String.valueOf(account_id) + "/move/new";
	}
	
	@PostMapping("/{id}/activate")
	public String Activate(Model model, @PathVariable(name="id")int id, @RequestParam(name="password") String pass) {
		
		Usuario user = uS.findById(id).get();
		user.setUpass(new BCryptPasswordEncoder().encode(pass));
		uS.insert(user);
		
		return "redirect:/client/home";
	}
}
