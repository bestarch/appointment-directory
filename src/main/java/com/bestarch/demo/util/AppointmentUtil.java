package com.bestarch.demo.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AppointmentUtil {
	
	public static final String APPOINTMENT_STATUS_NEW = "Submitted";
	
	public static final String APPOINTMENT_STATUS_CONFIRMED = "Confirmed";
	
	public static final String APPOINTMENT_STATUS_REJECTED = "Rejected";
	
	public String getUsername() {
		return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
