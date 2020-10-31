package pe.edu.upc.controller;

import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
//import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.upc.entity.Cliente;
import pe.edu.upc.entity.Cuenta;
import pe.edu.upc.entity.Moneda;
import pe.edu.upc.entity.Move;
import pe.edu.upc.entity.Usuario;
import pe.edu.upc.iservice.IClienteService;
import pe.edu.upc.iservice.ICuentaService;
import pe.edu.upc.iservice.IMoveService;
import pe.edu.upc.iservice.IUsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private IMoveService mS;
	
	@Autowired
	private IClienteService cS;
	
	@Autowired
	private IUsuarioService uS;
	
	@Autowired
	private ICuentaService cuS;
	
	@GetMapping("/home")
	public String goHome(Model model, Principal principal) {
		model.addAttribute("listCuentas", cuS.list());
		Usuario user = uS.findByUname(principal.getName()).get();
		
		model.addAttribute("perfil", user.getClient());
		
		return "/admin/home";
	}
	
	@GetMapping("/cuenta/new")
	public String goNewClient(Model model) {
		Cliente cliente = new Cliente();
		cliente.setCuenta(new Cuenta());
		cliente.setCuenta(new Cuenta());
		model.addAttribute("cliente", cliente);
		
		return "/admin/client/new";
	}
	
	@PostMapping("/cuenta/new")
	public String registerClient(@Valid Cliente client,
			BindingResult result, Model model,
			@RequestParam(name="mon") int moneda) {		
		if(result.hasErrors()) {
			model.addAttribute("cliente", client);

			result.getAllErrors().forEach(p->{
				System.out.println(p.getDefaultMessage());
			});
			
			return "/admin/client/new";
		}
		
		Usuario user = client.getAccount();
		Cuenta cuenta = client.getCuenta();
		
		//USER CONFIG
		String tempPass = "claro2018";
		user.setUpass(new BCryptPasswordEncoder().encode(tempPass));
		user.setIsadmin(false);
		user.setIsenabled(true);
		
		user.setClient(client);
		
		//CUENTA CONFIG
		cuenta.setMoneda(Moneda.values()[moneda]);
		cuenta.setBalance(cuenta.getMaxvalue());
		cuenta.setStart(new Date());
		cuenta.getDetail().setBankAccount(cuenta);
		
		cuenta.setOwner(client);
		
		try {
			uS.insert(user);
		} catch (Exception e) {
			model.addAttribute("cliente", client);
			
			model.addAttribute("error", e.getMessage());
			System.out.println(e.getMessage());
			
			return  "/admin/client/new";
		}
		return "redirect:/admin/home";
	}

	@GetMapping("/cuenta/{account_id}")
	public String goClientDetail(Model model, @PathVariable(name="account_id") int account_id) {
		
		Cuenta cuenta = cuS.findById(account_id).get();
		
		model.addAttribute("cuenta", cuenta);
		model.addAttribute("moveList", mS.list(cuenta));
		
		return "/admin/client/detail";
	}
	
	@GetMapping("/cuenta/{account_id}/new")
	public String goNewMove(Model model, @PathVariable(name="account_id") int account_id) {
		
		model.addAttribute("account_id", account_id);
		model.addAttribute("move", new Move());

		return "/admin/client/move/new";
	}
	
	@PostMapping("/cuenta/{account_id}/new")
	public String goNewMove(@Valid Move move, BindingResult result, Model model, @PathVariable(name="account_id") int account_id,
			 @RequestParam(name="positive", defaultValue = "false") boolean pos) {
		
		if(result.hasErrors()) {
			model.addAttribute("account_id", account_id);
			model.addAttribute("move", move);

			return "/admin/client/move/new";
		}
		
		if(pos == false) {
			move.setValue(move.getValue() * -1);
		}
		
		Cuenta cuenta = cuS.findById(account_id).get();

		Optional<Move> auxMmove = mS.getLast(cuenta);
		
		cuenta.setBalance((float) (cuenta.getBalance()+ move.getValue()));
		cuS.insert(cuenta);

		if(auxMmove.isPresent())
		{
			Move auxmove = auxMmove.get();
		
			if(auxmove.getCommit_date().compareTo(move.getCommit_date()) == 0) {
				move.setId(auxmove.getId());	
				move.setValue(move.getValue() + auxmove.getValue());
			}
		}
		move.setAccount(cuenta);
		
		try {
			mS.insert(move);			
		} catch (Exception e) {
			
			model.addAttribute("error", e.getMessage());
			return "/cuenta/" + String.valueOf(account_id) + "/new";
		}
				
		return "redirect:/admin/cuenta/" + String.valueOf(account_id);
	}
	
	
	@GetMapping("/client/list")
	public String goClientList(Model model) {
		model.addAttribute("listClientes", uS.list());
		return "/admin/client/list";
	}
	
	@GetMapping("/business/new")
	public String goNewBusiness(Model model) {
		//model.addAttribute("negocio",new Negocio());
		return "admin/business/new";
	}
	
	@GetMapping("/business/list")
	public String goBusinessList(Model model) {
		//model.addAttribute("listNegocio", nS.list());
		return "/admin/business/list";
	}
	
	@GetMapping("/admin/new")
	public String goNewAdmin(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "/admin/admin/new";
	}
	
	@GetMapping("/client/{id}/account/list")
	public String goCuentaList(@PathVariable(name="id") int id, Model model) {
		//Cliente cli = cS.findById(id).get();
		
		model.addAttribute("id", id);
		//model.addAttribute("listCuenta", cli.getCuentas());
		
		return "admin/client/account/list";
	}
	
	@GetMapping("/client/{id}/account/new")
	public String goNewCuenta(@PathVariable(name="id") int id, Model model) {
		model.addAttribute("id", id);
		model.addAttribute("cuenta", new Cuenta());
		
		return "admin/client/account/new";
	}
	
	@PostMapping("/business/new")
	public String registerBusiness(/*@Valid Negocio cli,*/BindingResult result, Model model) {		
		if(result.hasErrors()) {
			//model.addAttribute("negocio", cli);
			
			return "/admin/business/new";
		}
		
		//System.out.println(cli.getDesc());
		
		//if(cli.getDesc().trim() == "")
		//	cli.setDesc("");
		
		try {
			//nS.insert(cli);						
		} catch (Exception e) {
			//model.addAttribute("cliente", cli);
			
			model.addAttribute("error", e.getMessage());
			
			return  "/admin/business/new";
		}
		return "redirect:/admin/home";
	}
	
	@PostMapping("/admin/new")
	public String registerAdmin(@Valid Usuario user, BindingResult result, Model model) {		
		if(result.hasErrors()) {
			model.addAttribute("usuario", user);
			
			for(ObjectError err : result.getAllErrors()){
				System.out.println(err.getDefaultMessage());
			}
			
			return "/admin/admin/new";
		}
						
		user.setUpass(new BCryptPasswordEncoder().encode(user.getUpass()));
		user.setIsadmin(true);
		user.setIsenabled(true);
		
		try {
			uS.insert(user);						
		} catch (Exception e) {
			model.addAttribute("usuario", user);
			
			model.addAttribute("error", e.getMessage());
			
			return  "/admin/admin/new";
		}
		return "redirect:/admin/home";
	}

	@PostMapping("/client/{id}/account/new")
	public String RegisterAccount(@Valid Cuenta cuenta, BindingResult result, Model model,
			@PathVariable(name="id") int id, @RequestParam(name="length") int length, @RequestParam(name="mon") int moneda) {
		if(result.hasErrors()) {
			model.addAttribute("id", id);
			model.addAttribute("cuenta", cuenta);
			
			for(ObjectError err : result.getAllErrors()){
				System.out.println(err.getDefaultMessage());
			}
			
			return "/admin/client/account/new";
		}
		
		cuenta.setMoneda(Moneda.values()[moneda]);
		
		//cuenta.setBalance(cuenta.getLimit());
		
		cuenta.setOwner(cS.findById(id).get());
		
		Calendar c = Calendar.getInstance();
		c.setTime(cuenta.getStart());
		c.set(Calendar.DAY_OF_MONTH, 1);
		
		cuenta.setStart(c.getTime());
		
		c.add(Calendar.MONTH, length);
		
		//cuenta.setEnd(c.getTime());
		
		/*DetalleCuenta dc =  cuenta.getDetail();
		dc.setComApertura((float) (dc.getComApertura() * 0.01));
		dc.setComRenovar((float) (dc.getComRenovar() * 0.01));
		dc.setComDisponibildiad((float) (dc.getComDisponibildiad() * 0.01));
		
		dc.setIntDeudor((float) (dc.getIntDeudor() * 0.01));
		dc.setIntExcedente((float) (dc.getIntExcedente() * 0.01));
		dc.setIntExcedente((float) (dc.getIntAcreedor() * 0.01));
		
		//cuenta.setOpen(true);
		
		dc.setBankAccount(cuenta);
		*/
		System.out.println(	cuS.insert(cuenta));
		
		return "redirect:/admin/client/" + String.valueOf(id) + "/account/list";
	}
}
