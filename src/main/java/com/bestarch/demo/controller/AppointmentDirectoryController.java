package com.bestarch.demo.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bestarch.demo.domain.Appointment;
import com.bestarch.demo.service.AppointmentDirectoryService;
import com.bestarch.demo.util.AppointmentUtil;

@Controller
public class AppointmentDirectoryController {
	
	@Value("${stream.newappointment}")
    private String newAppointmentStream;

	@Autowired
	private AppointmentDirectoryService appointmentDirectoryService;
	
	@Autowired
	private AppointmentUtil appointmentUtil;
	
	@GetMapping(value = {"/", "/appointments"})
	public ModelAndView getAppointments() {
		List<Appointment> appointments = appointmentDirectoryService.getAppointments();
		ModelAndView mv = new ModelAndView("appointments");
        mv.addObject("appointments", appointments);
		return mv;
	}

	@GetMapping(value = "/new-appointment")
	public ModelAndView getNewEmployeeForm() {
		ModelAndView mv = new ModelAndView("new-appointment");
		String username = appointmentUtil.getUsername().getUsername();
		Appointment appt = new Appointment();
		appt.setUsername(username);
        mv.addObject(appt);
		return mv;
	}
	
	
	@PostMapping(value = "/appointment", consumes = {"application/x-www-form-urlencoded;charset=UTF-8"})
	public String addNewAppointment(@ModelAttribute Appointment appointment, BindingResult errors, Model model) {
		appointmentDirectoryService.addNewAppointment(appointment);
		return "redirect:/appointments";
	}
	
	@GetMapping(value = {"/logout"})
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

}