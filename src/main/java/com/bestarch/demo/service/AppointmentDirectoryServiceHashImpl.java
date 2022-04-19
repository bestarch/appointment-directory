package com.bestarch.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.stereotype.Service;

import com.bestarch.demo.domain.Appointment;
import com.bestarch.demo.domain.AppointmentRequestStream;
import com.bestarch.demo.repository.AppointmentCrudRepository;
import com.bestarch.demo.util.AppointmentUtil;

@Service
public class AppointmentDirectoryServiceHashImpl extends AppointmentDirectoryService {
	
	@Autowired
	private AppointmentCrudRepository appointmentCrudRepository;
	
	public Optional<Appointment> getAppointment(String appointmentId) {
		return appointmentCrudRepository.findById(appointmentId);
	}

	public List<Appointment> getAppointments() {
		List<Appointment> appointments = new ArrayList<>();
		appointmentCrudRepository.findAll().forEach(appointments::add);
		return appointments;
	}
	
	public Map<String, Integer> getAppointmentStats() {
		Map<String, Integer> map = new HashMap<>();
		
		return map;
	}

	public void addNewAppointment(Appointment appointment) {
		String createdTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String username = appointmentUtil.getUsername().getUsername();
		
		appointment.setStatus(AppointmentUtil.APPOINTMENT_STATUS_NEW);
		appointment.setCreatedTime(createdTime);
		appointment.setUsername(username);
		appointment.setUpdatedTime(null);
		appointmentCrudRepository.save(appointment);
		
		String apptDateStr = appointment.getAppointmentDate();
		LocalDateTime apptDate = LocalDateTime.parse(apptDateStr);
		String prefix = apptDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH", Locale.ENGLISH));
		String key = prefix+":appointment:"+username;
		
		AppointmentRequestStream apptRequest = AppointmentRequestStream.builder()
				.key(key)
				.createdTime(createdTime).build();
		ObjectRecord<String, AppointmentRequestStream> newAppointment = StreamRecords.newRecord()
                .ofObject(apptRequest)
                .withStreamKey(newAppointmentStream);
		
		redisTemplate.opsForStream().add(newAppointment);
	}

}
