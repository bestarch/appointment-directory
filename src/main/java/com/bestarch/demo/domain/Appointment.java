package com.bestarch.demo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@RedisHash("appointment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

	@Id
	private String username;
	private String name;
	private String appointmentId;
	private String contactNo;
	private String appointmentDate;
	private String status;
	private String description;
	private String createdTime;
	private String updatedTime;
}