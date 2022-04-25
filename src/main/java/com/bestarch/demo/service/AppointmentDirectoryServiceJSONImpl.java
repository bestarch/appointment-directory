package com.bestarch.demo.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.stereotype.Service;

import com.bestarch.demo.domain.Appointment;
import com.bestarch.demo.domain.AppointmentRequestStream;
import com.bestarch.demo.util.AppointmentUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redislabs.lettusearch.AggregateOptions;
import com.redislabs.lettusearch.AggregateOptions.Operation;
import com.redislabs.lettusearch.AggregateOptions.Operation.GroupBy.Reducer;
import com.redislabs.lettusearch.AggregateResults;
import com.redislabs.lettusearch.Document;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.SearchOptions;
import com.redislabs.lettusearch.SearchOptions.Limit;
import com.redislabs.lettusearch.SearchOptions.SortBy;
import com.redislabs.lettusearch.SearchResults;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import com.redislabs.modules.rejson.JReJSON;


@Service
@Primary
public class AppointmentDirectoryServiceJSONImpl extends AppointmentDirectoryService {
	
	@Autowired
	private JReJSON jreJSON; 
	
	private final static String QUERY = "*";
	private final static String AGGREGATE_QUERY = "@appointmentDateTime:[%d %d]";
	
	@Autowired
	private StatefulRediSearchConnection<String, String> connection;
	
	private final static ObjectMapper objectMapper = new ObjectMapper();
	
	public Optional<Appointment> getAppointment(String appointmentId) {
		return null;
	}

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
		
		SearchResults<String, String> results = commands.search("idx-createdTime", QUERY, searchOptions);
		
		Appointment ap = null;
		for (Document<String, String> doc : results) {
			Set<Entry<String, String>> entrySet = doc.entrySet();
			Iterator<Entry<String, String>> iter = entrySet.iterator();
			while (iter.hasNext()) {
				Entry<String, String> en = iter.next();
				if ("$".equals(en.getKey())) {
					try {
						ap = objectMapper.readValue(en.getValue(), Appointment.class);
						appointments.add(ap);
					} catch (Exception e) {
						e.printStackTrace();
						return new ArrayList<>();
					}
				}
			}
		}
		return appointments;
	}
	
	public SearchResults<String, String> getAppointments_v2(int offset, int page) {
		RediSearchCommands<String, String> commands = connection.sync();
		SortBy<String> sortBy = SortBy.<String>builder().field("createdTime").build();
		Limit limit = SearchOptions.Limit.builder().offset(offset).num(page).build();
		SearchOptions<String> searchOptions = SearchOptions
				.<String>builder()
				.sortBy(sortBy)
				.limit(limit)
				.build();
		SearchResults<String, String> results = commands.search("idx-createdTime", "*", searchOptions);
		return results;
	}
	
	public AggregateResults<String> getAppointmentStats() {
		RediSearchCommands<String, String> commands = connection.sync();
		Collection<String> grpBy = Arrays.asList(new String[] {"aptDate"});
		Reducer reducer = Reducer.Count.of("numOfAppts");
		Operation.SortBy.Property sortProperty = Operation.SortBy.Property.builder()
				.property("aptDate")
				.order(Operation.Order.Asc)
				.build();
		AggregateOptions aggregateOptions = AggregateOptions.builder()
				.apply("timefmt(@appointmentDateTime)", "aptDateTemp")
				.apply("substr(@aptDateTemp,0,10)", "aptDate")
				.groupBy(grpBy, reducer)
				.sortBy(sortProperty)
				.build();
		long fromDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
		long toDate = LocalDateTime.now().plusDays(14).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
		AggregateResults<String> result = commands.aggregate("idx-aptDate", String.format(AGGREGATE_QUERY, fromDate, toDate), aggregateOptions);
		System.out.println(result);
		return result;
	}

	public void addNewAppointment(Appointment appointment) {
		String username = appointmentUtil.getUsername();
		long createdTime = System.currentTimeMillis()/1000;
		appointment.setStatus(AppointmentUtil.APPOINTMENT_STATUS_NEW);
		appointment.setCreatedTime(createdTime);
		appointment.setUsername(username);
		
		String apptDateStr = appointment.getAppointmentDateStr();
		LocalDateTime apptDate = LocalDateTime.parse(apptDateStr);
		
		long suffix = (apptDate.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
		appointment.setAppointmentDateTime(suffix);
		String key = "appointment:"+username+":"+suffix;
		
		jreJSON.set(key, appointment);
		
		AppointmentRequestStream apptRequest = AppointmentRequestStream.builder()
				.key(key)
				.createdTime(createdTime).build();
		ObjectRecord<String, AppointmentRequestStream> newAppointment = StreamRecords.newRecord()
                .ofObject(apptRequest)
                .withStreamKey(newAppointmentStream);
		
		redisTemplate.opsForStream().add(newAppointment);
	}
	
	public static void main(String[] args) {
		System.out.println(LocalDateTime.now().plusDays(14).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
		System.out.println(System.currentTimeMillis());
		//String createdTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		//appointment.setAppointmentDate(apptDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)));
		//String suffix = apptDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh", Locale.ENGLISH));
	}

}
