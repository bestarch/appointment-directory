package com.bestarch.demo.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import com.bestarch.demo.domain.AppointmentRequestStream;
import com.bestarch.demo.util.AppointmentUtil;

@Component
public class AppointmentProcessor implements StreamListener<String, ObjectRecord<String, AppointmentRequestStream>> {
	
	@Value("${stream.newappointment}")
    private String newAppointmentStream;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	private SecureRandom random;
	
	@PostConstruct
	public void name() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed("ABC".getBytes("UTF-8"));
	}

	@Override
	public void onMessage(ObjectRecord<String, AppointmentRequestStream> record) {
		AppointmentRequestStream appointmentRequest = record.getValue();
		String key = appointmentRequest.getKey();
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
		}
		String status = AppointmentUtil.APPOINTMENT_STATUS_REJECTED;
		int generatedSuffix = random.nextInt(10000);
		if (generatedSuffix % 5 != 0) {
			status = AppointmentUtil.APPOINTMENT_STATUS_CONFIRMED;
			redisTemplate.opsForHash().put(key, "appointmentId", UUID.randomUUID().toString());
		} 
		redisTemplate.opsForHash().put(key, "status", status);
		redisTemplate.opsForHash().put(key, "updatedTime", String.valueOf(System.currentTimeMillis()/1000));
	}

}
