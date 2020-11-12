package pe.edu.upc.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import pe.edu.upc.iservice.ICuentaService;
import pe.edu.upc.iservice.IMoveService;
import pe.edu.upc.iservice.IUsuarioService;

@Controller
@RequestMapping("/admin")
@Secured("ROLE_ADMIN")
public class AdminController {
	
	@Autowired
	private IMoveService mS;
		
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
		model.addAttribute("client", cuenta.getOwner());
		
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
	
	@GetMapping("/cuenta/{id_account}/reports")
	public String goReports(Model model, @PathVariable(name="id_account") int account_id) {
		
		Cuenta cuenta = cuS.findById(account_id).get();
		List<String[]> data = new ArrayList<String[]>();

		DateFormat dF = new SimpleDateFormat( "dd-MM-yyyy");
		DecimalFormat dF2 = new DecimalFormat("#.####	");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(cuenta.getStart());
		
		Calendar limitCal = Calendar.getInstance();
		limitCal.setTime(cal.getTime());
		limitCal.add(Calendar.MONTH, cuenta.getPayment_period());
		
		System.out.println("limit: " + String.valueOf(limitCal.getTime()));
		
		double balance = cuenta.getMaxvalue();
		
		List<Move> moves = mS.list(cuenta);
		Calendar cal2  = Calendar.getInstance();
		
		float intDeudor = cuenta.getDetail().getIntDeudor() / 360;
		double Intereces = 0;
				
		int  accDays = 0;
		
		for(Move p: moves) {
			cal2.setTime(p.getCommit_date());
			System.out.println("[D][" + String.valueOf(cal.getTime()) + ";" + String.valueOf(cal2.getTime()) + "]["+ String.valueOf(balance)+"]["+ String.valueOf(p.getValue())+"]");
			
			double tmpVal = cuenta.getMaxvalue() - balance;

			if(limitCal.getTime().compareTo(p.getCommit_date()) < 0) {
				System.out.println("jumped");
		
				int DayDiff = -(cal.get(Calendar.YEAR)*360 + cal.get(Calendar.DAY_OF_YEAR) - limitCal.get(Calendar.YEAR)*360 - limitCal.get(Calendar.DAY_OF_YEAR));
				
				System.out.println("[J][" + String.valueOf(cal.getTime()) + ";" + String.valueOf(limitCal.getTime()) + "][" + String.valueOf(DayDiff) + "][" + String.valueOf(intDeudor)+ "][" + String.valueOf(tmpVal)+ "]");
				
				Intereces += tmpVal * intDeudor *  DayDiff;
				balance += p.getValue();
				
				cal.setTime(limitCal.getTime());
				
				String[] dat = {
						dF.format(limitCal.getTime()) ,  dF2.format(Intereces), String.valueOf(accDays + DayDiff)
				};
				data.add(dat);
				accDays = 0;
				limitCal.add(Calendar.MONTH, cuenta.getPayment_period());
				Intereces = 0;
				
			}
				
			int DayDiff = -(cal.get(Calendar.YEAR)*360 + cal.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.YEAR)*360 - cal2.get(Calendar.DAY_OF_YEAR));						System.out.println("[R][" + String.valueOf(cal.getTime()) + ";" + String.valueOf(cal2.getTime()) + "][" + String.valueOf(DayDiff) + "][" + String.valueOf(intDeudor)+ "][" + String.valueOf(tmpVal)+ "]");
	
			Intereces += tmpVal * intDeudor *  DayDiff;
						balance += p.getValue();
				cal.setTime(cal2.getTime());
			
			accDays += DayDiff;
		}
		
		//lastmove
		
		cal2.setTime(new Date());
		System.out.println("[" + String.valueOf(cal.getTime()) + ";" + String.valueOf(cal2.getTime()) + "]["+ String.valueOf(balance)+"][end]");
		double tmpVal = cuenta.getMaxvalue() - balance;
				
		while(true) {
			if(limitCal.compareTo(cal2) < 0) {
				int DayDiff = -(cal.get(Calendar.YEAR)*360 + cal.get(Calendar.DAY_OF_YEAR) - limitCal.get(Calendar.YEAR)*360 - limitCal.get(Calendar.DAY_OF_YEAR));
				
				System.out.println("[" + String.valueOf(cal.getTime()) + ";" + String.valueOf(limitCal.getTime()) + "][" + String.valueOf(DayDiff) + "][" + String.valueOf(intDeudor)+ "][" + String.valueOf(tmpVal)+ "]");
		
				Intereces += tmpVal * intDeudor *  DayDiff;
				
				cal.setTime(limitCal.getTime());
				String[] dat = {
						dF.format(limitCal.getTime()), dF2.format(Intereces), String.valueOf(DayDiff)
				};
				data.add(dat);
				
				limitCal.add(Calendar.MONTH, cuenta.getPayment_period());
				Intereces = 0;				
			}else {
				int DayDiff = -(cal.get(Calendar.YEAR)*360 + cal.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.YEAR)*360 - cal2.get(Calendar.DAY_OF_YEAR));
				
				System.out.println("[" + String.valueOf(cal.getTime()) + ";" + String.valueOf(cal2.getTime()) + "][" + String.valueOf(DayDiff) + "][" + String.valueOf(intDeudor)+ "][" + String.valueOf(tmpVal)+ "]");
		
				Intereces += tmpVal * intDeudor *  DayDiff;				
				break;
			}			
		}

		data.forEach(p->{
			System.out.println(p[0] + ": " + p[1]);
		});
		
		model.addAttribute("account_id", account_id);
		model.addAttribute("listPayments", data);
		model.addAttribute("today", dF.format(new Date()));
		model.addAttribute("intereces", dF2.format(Intereces));
		
		return "/admin/client/move/detail";
	}
	
	
}
