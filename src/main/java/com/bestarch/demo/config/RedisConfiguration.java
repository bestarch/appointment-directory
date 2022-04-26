package com.bestarch.demo.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.data.redis.stream.Subscription;

import com.bestarch.demo.domain.AppointmentRequestStream;
//import com.redislabs.modules.rejson.JReJSON;
import com.redislabs.modules.rejson.JReJSON;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;

@Configuration
class RedisConfiguration {
	
	@Value("${stream.newappointment}")
    private String newAppointmentStream;

	@Value("${spring.redis.host:localhost}")
	private String server;

	@Value("${spring.redis.port:6379}")
	private String port;

	@Value("${spring.redis.password}")
	private String pswd;
	
	@Autowired
    private StreamListener<String, ObjectRecord<String, AppointmentRequestStream>> streamListener;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(server, Integer.valueOf(port));
		RedisPassword rp = RedisPassword.of(pswd);
		redisStandaloneConfiguration.setPassword(rp);
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}
	
	
	@Bean
	public JReJSON jreJSON() {
		HostAndPort hostAndPort = new HostAndPort(server, Integer.valueOf(port));
		JedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder().password(pswd).build();
		Jedis jedis = new Jedis(hostAndPort, jedisClientConfig);
		JReJSON jreJSON = new JReJSON(jedis);
		return jreJSON;
	}
	
	@Bean
	public RedisTemplate<String, String> redisTemplate() {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();

		template.setConnectionFactory(redisConnectionFactory());

		template.setKeySerializer(stringSerializer);
		template.setHashKeySerializer(stringSerializer);

		template.setValueSerializer(stringSerializer);
		template.setHashValueSerializer(stringSerializer);

		template.setEnableTransactionSupport(true);
		template.afterPropertiesSet();
		try {
			template.opsForStream().createGroup(newAppointmentStream, newAppointmentStream);
		} catch (DataAccessException e) {
			System.out.println("Ognoring the exception. Redis Stream group may be present already. Skipping it");
			e.printStackTrace();
		}
		return template;
	}
	
	@Bean
    public Subscription subscription(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
		
    	StreamMessageListenerContainerOptions<String, ObjectRecord<String, AppointmentRequestStream>> options = StreamMessageListenerContainer
                            .StreamMessageListenerContainerOptions
                            .builder()
                            .pollTimeout(Duration.ofSeconds(20))
                            .targetType(AppointmentRequestStream.class)
                            .build();
    	
    	StreamMessageListenerContainer<String, ObjectRecord<String, AppointmentRequestStream>> listenerContainer = StreamMessageListenerContainer.create(redisConnectionFactory, options);
    	
    	Subscription subscription = listenerContainer.receiveAutoAck(
                Consumer.from(newAppointmentStream, InetAddress.getLocalHost().getHostName()),
                StreamOffset.create(newAppointmentStream, ReadOffset.lastConsumed()),
                streamListener);
        listenerContainer.start();
        return subscription;
    }
	
}