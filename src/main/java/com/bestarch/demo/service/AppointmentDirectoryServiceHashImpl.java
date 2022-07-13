package com.bestarch.demo.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.stereotype.Service;

import com.bestarch.demo.domain.Appointment;
import com.bestarch.demo.domain.AppointmentRequestStream;
import com.bestarch.demo.domain.UserProfile;
import com.bestarch.demo.repository.AppointmentCrudRepository;
import com.bestarch.demo.util.AppointmentUtil;
import com.redislabs.lettusearch.AggregateResults;
import com.redislabs.lettusearch.SearchResults;

@Service
public class AppointmentDirectoryServiceHashImpl extends AppointmentDirectoryService {
	
	@Autowired
	private AppointmentCrudRepository appointmentCrudRepository;
	
	public Optional<Appointment> getAppointment(String appointmentId) {
		return appointmentCrudRepository.findById(appointmentId);
	}

	public List<Appointment> getAppointments(int offset, int page) {
		List<Appointment> appointments = new ArrayList<>();
		appointmentCrudRepository.findAll().forEach(appointments::add);
		return appointments;
	}
	
	public AggregateResults<String> getAppointmentStats() {
		throw new RuntimeException("Not implemented");
	}

	public void addNewAppointment(Appointment appointment) {
		String username = appointmentUtil.getUsername();
		long createdTime = System.currentTimeMillis()/1000;
		appointment.setStatus(AppointmentUtil.APPOINTMENT_STATUS_NEW);
		appointment.setCreatedTime(createdTime);
		appointment.setUsername(username);
		appointmentCrudRepository.save(appointment);
		
		String apptDateStr = appointment.getAppointmentDateStr();
		LocalDateTime apptDate = LocalDateTime.parse(apptDateStr);
		
		long suffix = (apptDate.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
		
		String key = "appointment:"+username+":"+suffix;
		
		AppointmentRequestStream apptRequest = AppointmentRequestStream.builder()
				.key(key)
				.createdTime(createdTime).build();
		ObjectRecord<String, AppointmentRequestStream> newAppointment = StreamRecords.newRecord()
                .ofObject(apptRequest)
                .withStreamKey(newAppointmentStream);
		
		redisTemplate.opsForStream().add(newAppointment);
	}

	@Override
	public SearchResults<String, String> getAppointments_v2(int offset, int page) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void saveUserProfile(UserProfile userProfile) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Optional<UserProfile> getUserProfile(String username) {
		throw new RuntimeException("Not implemented");
	}

}
