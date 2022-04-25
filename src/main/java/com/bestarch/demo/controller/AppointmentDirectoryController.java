package com.bestarch.demo.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bestarch.demo.domain.Appointment;
import com.bestarch.demo.service.AppointmentDirectoryService;
import com.bestarch.demo.util.AppointmentUtil;
import com.redislabs.lettusearch.AggregateResults;
import com.redislabs.lettusearch.SearchResults;

@Controller
public class AppointmentDirectoryController {
	
	@Value("${stream.newappointment}")
    private String newAppointmentStream;

	@Autowired
	private AppointmentDirectoryService appointmentDirectoryService;
	
	@Autowired
	private AppointmentUtil appointmentUtil;
	
	@GetMapping(value = {"/", "/appointments"})
	public ModelAndView getAppointments(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int page) {
		List<Appointment> appointments = appointmentDirectoryService.getAppointments(offset, page);
		AggregateResults<String> result = appointmentDirectoryService.getAppointmentStats();
		List<String> dates = new ArrayList<>();
		List<Integer> count = new ArrayList<>();
		result.stream().forEach(m -> {
			dates.add((String)m.get("aptDate"));
			count.add(Integer.valueOf((String)m.get("numOfAppts")));
		});
		ModelAndView mv = new ModelAndView("appointments");
        mv.addObject("appointments", appointments);
        mv.addObject("dates", dates);
        mv.addObject("count", count);
		return mv;
	}
	
	@GetMapping(value = {"/appointments_v2"})
	public @ResponseBody SearchResults<String, String> getAppointmentse(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int page) {
		SearchResults<String, String> appointments = appointmentDirectoryService.getAppointments_v2(offset, page);
		return appointments;
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