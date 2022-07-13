package com.bestarch.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bestarch.demo.domain.Appointment;
import com.bestarch.demo.domain.UserProfile;
import com.bestarch.demo.util.AppointmentUtil;
import com.redislabs.lettusearch.AggregateResults;
import com.redislabs.lettusearch.SearchResults;

@Service
public abstract class AppointmentDirectoryService {
	
	@Value("${stream.newappointment}")
    protected String newAppointmentStream;

	@Autowired
	protected RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	protected AppointmentUtil appointmentUtil;
	
	public abstract Optional<Appointment> getAppointment(String appointmentId);

	public abstract List<Appointment> getAppointments(int offset, int page);
	
	public abstract SearchResults<String, String> getAppointments_v2(int offset, int page);
	
	public abstract AggregateResults<String> getAppointmentStats();
	
	public abstract void addNewAppointment(Appointment appointment);
	
	public abstract void saveUserProfile(UserProfile userProfile);

	public abstract Optional<UserProfile> getUserProfile(String username);

}
