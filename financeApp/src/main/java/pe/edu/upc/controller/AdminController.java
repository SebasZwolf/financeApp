package pe.edu.upc.controller;

import java.util.Calendar;
import java.util.Date;

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
import org.thymeleaf.util.DateUtils;

import pe.edu.upc.entity.Cliente;
import pe.edu.upc.entity.Cuenta;
import pe.edu.upc.entity.DetalleCuenta;
import pe.edu.upc.entity.Moneda;
import pe.edu.upc.entity.Negocio;
import pe.edu.upc.entity.Usuario;
import pe.edu.upc.iservice.IClienteService;
import pe.edu.upc.iservice.ICuentaService;
import pe.edu.upc.iservice.INegocioService;
import pe.edu.upc.iservice.IUsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private IClienteService cS;
	
	@Autowired
	private IUsuarioService uS;
	
	@Autowired
	private INegocioService nS;
	
	@Autowired
	private ICuentaService cuS;
	
	@GetMapping("/home")
	public String goHome() {
		return "/admin/home";
	}
	
	@GetMapping("/client/new")
	public String goNewClient(Model model) {
		model.addAttribute("cliente", new Cliente());
		model.addAttribute("listNegocio", nS.list());
		
		return "/admin/client/new";
	}
	
	@GetMapping("/client/list")
	public String goClientList(Model model) {
		model.addAttribute("listClientes", uS.list());
		return "/admin/client/list";
	}
	
	@GetMapping("/business/new")
	public String goNewBusiness(Model model) {
		model.addAttribute("negocio",new Negocio());
		return "admin/business/new";
	}
	
	@GetMapping("/business/list")
	public String goBusinessList(Model model) {
		model.addAttribute("listNegocio", nS.list());
		return "/admin/business/list";
	}
	
	@GetMapping("/admin/new")
	public String goNewAdmin(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "/admin/admin/new";
	}
	
	@GetMapping("/client/{id}/account/list")
	public String goCuentaList(@PathVariable(name="id") int id, Model model) {
		Cliente cli = cS.findById(id).get();
		
		model.addAttribute("id", id);
		model.addAttribute("listCuenta", cli.getCuentas());
		
		return "admin/client/account/list";
	}
	
	@GetMapping("/client/{id}/account/new")
	public String goNewCuenta(@PathVariable(name="id") int id, Model model) {
		model.addAttribute("id", id);
		model.addAttribute("cuenta", new Cuenta());
		
		return "admin/client/account/new";
	}
	
	@PostMapping("/client/new")
	public String registerClient(@Valid Cliente cli, BindingResult result, Model model) {		
		if(result.hasErrors()) {
			model.addAttribute("cliente", cli);
			model.addAttribute("listNegocio", nS.list());
			
			return "/admin/client/new";
		}
		
		String tempPass = "claro2018";
		
		Usuario user = cli.getAccount();
		
		user.setUpass(new BCryptPasswordEncoder().encode(tempPass));
		user.setIsadmin(false);
		user.setIsenabled(true);
		
		try {
			cS.insert(cli);						
		} catch (Exception e) {
			model.addAttribute("cliente", cli);
			model.addAttribute("listNegocio", nS.list());
			
			model.addAttribute("error", e.getMessage());
			
			return  "/admin/client/new";
		}
		return "redirect:/admin/home";
	}

	@PostMapping("/business/new")
	public String registerBusiness(@Valid Negocio cli, BindingResult result, Model model) {		
		if(result.hasErrors()) {
			model.addAttribute("negocio", cli);
			
			return "/admin/business/new";
		}
		
		System.out.println(cli.getDesc());
		
		if(cli.getDesc().trim() == "")
			cli.setDesc("");
		
		try {
			nS.insert(cli);						
		} catch (Exception e) {
			model.addAttribute("cliente", cli);
			
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
	public String RegisterAccount(@Valid Cuenta cuenta, BindingResult result, Model model, @PathVariable(name="id") int id, @RequestParam(name="length") int length, @RequestParam(name="mon") int moneda) {
		if(result.hasErrors()) {
			model.addAttribute("id", id);
			model.addAttribute("cuenta", cuenta);
			
			for(ObjectError err : result.getAllErrors()){
				System.out.println(err.getDefaultMessage());
			}
			
			return "/admin/client/account/new";
		}
		
		cuenta.setMoneda(Moneda.values()[moneda]);
		
		cuenta.setBalance(cuenta.getLimit());
		
		cuenta.setOwner(cS.findById(id).get());
		
		Calendar c = Calendar.getInstance();
		c.setTime(cuenta.getStart());
		c.set(Calendar.DAY_OF_MONTH, 1);
		
		cuenta.setStart(c.getTime());
		
		c.add(Calendar.MONTH, length);
		
		cuenta.setEnd(c.getTime());
		
		DetalleCuenta dc =  cuenta.getDetail();
		dc.setComApertura((float) (dc.getComApertura() * 0.01));
		dc.setComRenovar((float) (dc.getComRenovar() * 0.01));
		dc.setComDisponibildiad((float) (dc.getComDisponibildiad() * 0.01));
		
		dc.setIntDeudor((float) (dc.getIntDeudor() * 0.01));
		dc.setIntExcedente((float) (dc.getIntExcedente() * 0.01));
		dc.setIntExcedente((float) (dc.getIntAcreedor() * 0.01));
		
		cuenta.setOpen(true);
		
		dc.setBankAccount(cuenta);
		
		System.out.println(	cuS.insert(cuenta));
		
		return "redirect:/admin/client/" + String.valueOf(id) + "/account/list";
	}
}
