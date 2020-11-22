package pe.edu.upc.controller;

import java.lang.ProcessBuilder.Redirect;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		
		Cuenta cuenta = cuS.findById(account_id).get();
		
		model.addAttribute("account_id", account_id);
		model.addAttribute("cuenta", cuenta);
		model.addAttribute("move", new Move());

		return "/admin/client/move/new";
	}
	
	@PostMapping("/cuenta/{account_id}/new")
	public String goNewMove(@Valid Move move, BindingResult result, Model model, @PathVariable(name="account_id") int account_id,
			 @RequestParam(name="positive", defaultValue = "false") boolean pos, RedirectAttributes objRedirect) {
		
		Cuenta cuenta = cuS.findById(account_id).get();

		if(result.hasErrors()) {
			
			model.addAttribute("account_id", account_id);
			model.addAttribute("cuenta", cuenta);
			model.addAttribute("move", move);

			return "/admin/client/move/new";
		}
		
		if(pos == false) {
			move.setValue(move.getValue() * -1);
		}
		
		if(!pos && -move.getValue() > cuenta.getBalance())
		{
			model.addAttribute("account_id", account_id);
			model.addAttribute("cuenta", cuenta);

			move.setValue(move.getValue() * -1);
			model.addAttribute("move", move);

			model.addAttribute("error", "valor limite de deuda exedido! (max" + String.valueOf(cuenta.getBalance()) + ")");
			
			return "/admin/client/move/new";
		}
		
		if(pos && (move.getValue() + cuenta.getBalance()) > cuenta.getMaxvalue()) {
			double exedido = (move.getValue() + cuenta.getBalance()) - cuenta.getMaxvalue();
			
			objRedirect.addFlashAttribute("vuelto", exedido);
			//model.addAttribute("message", "vuelto: " + String.valueOf(exedido));
			move.setValue(move.getValue() - exedido);
		}
		
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
		DecimalFormat dF2 = new DecimalFormat("#.####");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(cuenta.getStart());
		
		Calendar limitCal = Calendar.getInstance();
		limitCal.setTime(cal.getTime());
		limitCal.add(Calendar.MONTH, cuenta.getPayment_period());
		
		System.out.println("limit: " +dF.format(limitCal.getTime()));
		
		//double balance = cuenta.getMaxvalue();
		
		List<Move> moves = mS.list(cuenta);
		Calendar cal2  = Calendar.getInstance();
		
		float intDeudor = cuenta.getDetail().getIntDeudor() / 360;
		double Intereces = 0;
		
		System.out.println("interez deudor: " + String.valueOf(intDeudor));
		
		int  accDays = 0;
		
		double deudaAcumulada = 0;
		
		int NPay = 1;
		
		for(Move p: moves) {
			
			cal2.setTime(p.getCommit_date());
			System.out.println("\u001B[0m" + "[D][" 
					+ dF.format(cal.getTime()) + 								";"
					+ dF.format(cal2.getTime()) +								"]["
					+ String.valueOf(deudaAcumulada)+					"]["
					+ String.valueOf(p.getValue())+							"]" + "\u001B[0m");
						
			while(limitCal.getTime().compareTo(p.getCommit_date()) < 0) {		
				int DayDiff = -(cal.get(Calendar.YEAR)*360 + cal.get(Calendar.DAY_OF_YEAR) - limitCal.get(Calendar.YEAR)*360 - limitCal.get(Calendar.DAY_OF_YEAR));
				
				System.out.println("[J][" + dF.format(cal.getTime()) + ";" + dF.format(limitCal.getTime()) + "][" + String.valueOf(DayDiff) +"][" + String.valueOf(deudaAcumulada)+ "]");
				
				Intereces += deudaAcumulada * intDeudor *  DayDiff;
				
				cal.setTime(limitCal.getTime());
				
				String[] dat = {
						dF.format(limitCal.getTime()) ,  dF2.format(Intereces), String.valueOf(accDays + DayDiff), String.valueOf(cuenta.getPagados().contains(NPay))
				};
				
				NPay++;
				
				data.add(dat);
				accDays = 0;
				limitCal.add(Calendar.MONTH, cuenta.getPayment_period());
				Intereces = 0;	
			}
				
			int DayDiff = -(cal.get(Calendar.YEAR)*360 + cal.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.YEAR)*360 - cal2.get(Calendar.DAY_OF_YEAR));						System.out.println("[R][" + dF.format(cal.getTime()) + ";" + dF.format(cal2.getTime()) + "][" + String.valueOf(DayDiff) + "]["+ String.valueOf(deudaAcumulada)+ "]");
	
			Intereces += deudaAcumulada * intDeudor *  DayDiff;
						deudaAcumulada += -p.getValue();
				cal.setTime(cal2.getTime());
			
			accDays += DayDiff;
		}
		
		//lastmove
		
		cal2.setTime(new Date());
		System.out.println("[" + dF.format(cal.getTime()) + ";" + dF.format(cal2.getTime()) + "]["+ String.valueOf(deudaAcumulada)+"][end]");
		//double tmpVal = cuenta.getMaxvalue() - balance;
				
		while(true) {
			if(limitCal.compareTo(cal2) < 0) {
				int DayDiff = -(cal.get(Calendar.YEAR)*360 + cal.get(Calendar.DAY_OF_YEAR) - limitCal.get(Calendar.YEAR)*360 - limitCal.get(Calendar.DAY_OF_YEAR));
				
				System.out.println("[" + dF.format(cal.getTime()) + ";" +dF.format(limitCal.getTime()) + "][" + String.valueOf(DayDiff) + String.valueOf(deudaAcumulada)+ "]");
		
				Intereces += deudaAcumulada * intDeudor *  DayDiff;
				
				cal.setTime(limitCal.getTime());
				String[] dat = {
						dF.format(limitCal.getTime()), dF2.format(Intereces), String.valueOf(DayDiff), String.valueOf(cuenta.getPagados().contains(NPay))
				};
				NPay++;
				
				data.add(dat);
				
				limitCal.add(Calendar.MONTH, cuenta.getPayment_period());
				Intereces = 0;				
			}else {
				int DayDiff = -(cal.get(Calendar.YEAR)*360 + cal.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.YEAR)*360 - cal2.get(Calendar.DAY_OF_YEAR));
				
				System.out.println("[" + dF.format(cal.getTime()) + ";" + dF.format(cal2.getTime()) + "][" + String.valueOf(DayDiff) + "][" + String.valueOf(deudaAcumulada)+ "]");
		
				Intereces += deudaAcumulada * intDeudor *  DayDiff;				
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
	
	@PostMapping("/cuenta/{id_account}/reports/pay")
	public String registerPayment(Model model, @PathVariable(name="id_account") int id_account, @RequestParam(name="npay", defaultValue = "0") int npay) {
		System.out.print("llegando! ");
		System.out.println(npay);
		
		if(npay == 0)
			return "redirect:/admin/cuenta/" + String.valueOf(id_account) + "/reports" ;
		
		Cuenta cuenta = cuS.findById(id_account).get();
		
		if(cuenta.getPagados().contains(npay))
			return "redirect:/admin/cuenta/" + String.valueOf(id_account) + "/reports" ;
		
		
		System.out.print("llegaste!");
		cuS.setPaid(cuenta, npay);
		
			
		return "redirect:/admin/cuenta/" + String.valueOf(id_account) + "/reports" ;
	}
}
