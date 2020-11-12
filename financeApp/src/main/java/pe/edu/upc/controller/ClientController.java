package pe.edu.upc.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pe.edu.upc.entity.Cuenta;
import pe.edu.upc.entity.Move;
import pe.edu.upc.entity.Usuario;
import pe.edu.upc.iservice.IMoveService;
import pe.edu.upc.iservice.IUsuarioService;

@Controller
@RequestMapping("/client")
@Secured("ROLE_USER")
public class ClientController {

	@Autowired
	private IUsuarioService uS;

	@Autowired
	private IMoveService mS;
	
	@GetMapping("/home")
	public String goHome(Model model, Authentication auth) {
		Usuario user = uS.findByUname(auth.getName()).get();
		
		Cuenta cuenta = user.getClient().getCuenta();
		
		model.addAttribute("cuenta", cuenta);
		model.addAttribute("moveList", mS.list(cuenta));
		model.addAttribute("client", cuenta.getOwner());
		
		return "/client/home";
	}

	@GetMapping("/reports")
	public String goReports(Model model, Principal principal) {
		
		
		

		Cuenta cuenta =uS.findByUname(principal.getName()).get().getClient().getCuenta();
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
				
			int DayDiff = -(cal.get(Calendar.YEAR)*360 + cal.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.YEAR)*360 - cal2.get(Calendar.DAY_OF_YEAR));
			
			System.out.println("[R][" + String.valueOf(cal.getTime()) + ";" + String.valueOf(cal2.getTime()) + "][" + String.valueOf(DayDiff) + "][" + String.valueOf(intDeudor)+ "][" + String.valueOf(tmpVal)+ "]");
	
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
		
		model.addAttribute("listPayments", data);
		model.addAttribute("today", dF.format(new Date()));
		model.addAttribute("intereces", dF2.format(Intereces));
				
		return "/client/reports";
	}
	
}
