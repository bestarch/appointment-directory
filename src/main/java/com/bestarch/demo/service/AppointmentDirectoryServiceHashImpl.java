package com.bestarch.demo.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.stereotype.Service;

import com.bestarch.demo.domain.Appointment;
import com.bestarch.demo.domain.AppointmentRequestStream;
import com.bestarch.demo.domain.UserProfile;
import com.bestarch.demo.repository.UserProfileCrudRepository;
import com.bestarch.demo.util.AppointmentUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redislabs.lettusearch.Document;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.SearchOptions;
import com.redislabs.lettusearch.SearchOptions.Limit;
import com.redislabs.lettusearch.SearchOptions.SortBy;
import com.redislabs.lettusearch.SearchResults;
import com.redislabs.lettusearch.StatefulRediSearchConnection;

@Service
@Primary
public class AppointmentDirectoryServiceHashImpl extends AppointmentDirectoryService {
	
	private final static String QUERY = "*";
	
	private final static ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private UserProfileCrudRepository userProfileCrudRepository;
	
	@Autowired
	private StatefulRediSearchConnection<String, String> connection;
	

	public List<Appointment> getAppointments(int offset, int page) {
		List<Appointment> appointments = new ArrayList<>();
		RediSearchCommands<String, String> commands = connection.sync();
		SortBy<String> sortBy = SortBy.<String>builder().field("createdTime").build();
		Limit limit = SearchOptions.Limit.builder().offset(offset).num(page).build();
		SearchOptions<String> searchOptions = SearchOptions
				.<String>builder()
				.sortBy(sortBy)
				.limit(limit)
				.build();
		
		SearchResults<String, String> results = commands.search("idx-createdTime-v2", QUERY, searchOptions);
		
		for (Document<String, String> doc : results) {
			Appointment appt = objectMapper.convertValue(doc, Appointment.class);
			appointments.add(appt);
		}
		return appointments;
	}

	public void addNewAppointment(Appointment appointment) {
		String username = appointmentUtil.getUsername();
		long createdTime = System.currentTimeMillis()/1000;
		String apptDateStr = appointment.getAppointmentDateStr();
		LocalDateTime apptDate = LocalDateTime.parse(apptDateStr);
		long suffix = (apptDate.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());

		String key = "appointment:"+username+":"+suffix;
		
		Map<String, Object> values = new HashMap<>();
		values.put("status", AppointmentUtil.APPOINTMENT_STATUS_NEW);
		values.put("createdTime", String.valueOf(createdTime));
		values.put("description", appointment.getDescription());
		values.put("contactNo", appointment.getContactNo());
		values.put("username", username);
		values.put("doctorName", appointment.getDoctorName());
		values.put("appointmentId", appointment.getAppointmentId());
		values.put("appointmentDateTime", String.valueOf(suffix));
		values.put("appointmentDateStr", appointment.getAppointmentDateStr());
		values.put("updatedTime", String.valueOf(appointment.getUpdatedTime()));
		
		redisTemplate.opsForHash().putAll(key, values);
		
		AppointmentRequestStream apptRequest = AppointmentRequestStream.builder()
				.key(key)
				.createdTime(createdTime).build();
		ObjectRecord<String, AppointmentRequestStream> newAppointment = StreamRecords.newRecord()
                .ofObject(apptRequest)
                .withStreamKey(newAppointmentStream);
		
		redisTemplate.opsForStream().add(newAppointment);
	}

	@Override
	public void saveUserProfile(UserProfile userProfile) {
		String username = appointmentUtil.getUsername();
		userProfile.setUsername(username);
		userProfileCrudRepository.save(userProfile);
	}

	@Override
	public Optional<UserProfile> getUserProfile(String username) {
		return userProfileCrudRepository.findById(username);
	}

}
