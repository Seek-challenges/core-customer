package pe.seek.core.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import pe.seek.core.customer.domain.Customer;

@Configuration
class RedisConfig {

    @Value("${spring.data.redis.host}")
    String redisHost;

    @Value("${spring.data.redis.port}")
    Integer redisPort;

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory redisVirtualConnectionFactory(@Qualifier("ASYNC_VIRTUAL_THREAD_TASK_EXECUTOR") AsyncTaskExecutor executor) {
        LettuceConnectionFactory redisStandaloneConfiguration = new LettuceConnectionFactory(redisHost, redisPort);
        redisStandaloneConfiguration.setShareNativeConnection(false);
        redisStandaloneConfiguration.setExecutor(executor);
        return redisStandaloneConfiguration;
    }

    @Bean
    public ReactiveRedisTemplate<String, Customer> reactiveRedisTemplate(
            ObjectMapper objectMapper,
            @Qualifier("redisVirtualConnectionFactory") ReactiveRedisConnectionFactory factory
    ) {
        Jackson2JsonRedisSerializer<Customer> valueSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Customer.class);
        Jackson2JsonRedisSerializer<String> keySerializer = new Jackson2JsonRedisSerializer<>(String.class);

        RedisSerializationContext<String, Customer> context = RedisSerializationContext
                .<String, Customer>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
