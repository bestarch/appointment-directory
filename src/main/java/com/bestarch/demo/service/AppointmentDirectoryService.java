package com.bestarch.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bestarch.demo.domain.Appointment;
import com.bestarch.demo.domain.AppointmentRequestStream;
import com.bestarch.demo.repository.AppointmentCrudRepository;
import com.bestarch.demo.util.AppointmentUtil;

@Service
public class AppointmentDirectoryService {
	
	@Value("${stream.newappointment}")
    private String newAppointmentStream;

	@Autowired
	private AppointmentCrudRepository appointmentCrudRepository;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private AppointmentUtil appointmentUtil;
	
	public Optional<Appointment> getAppointment(String appointmentId) {
		return appointmentCrudRepository.findById(appointmentId);
	}

	public List<Appointment> getAppointments() {
		List<Appointment> appointments = new ArrayList<>();
		appointmentCrudRepository.findAll().forEach(appointments::add);
		return appointments;
	}

	public void addNewAppointment(Appointment appointment) {
		String createdTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String username = appointmentUtil.getUsername().getUsername();
		
		appointment.setStatus(AppointmentUtil.APPOINTMENT_STATUS_NEW);
		appointment.setCreatedTime(createdTime);
		appointment.setUsername(username);
		appointment.setUpdatedTime(null);
		appointmentCrudRepository.save(appointment);
		
		AppointmentRequestStream apptRequest = AppointmentRequestStream.builder()
				.createdTime(createdTime)
				.username(username).build();
		ObjectRecord<String, AppointmentRequestStream> newAppointment = StreamRecords.newRecord()
                .ofObject(apptRequest)
                .withStreamKey(newAppointmentStream);
		
		redisTemplate.opsForStream().add(newAppointment);
	}

}
