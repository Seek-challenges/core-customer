package pe.seek.core.config.broker.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

import static pe.seek.core.shared.constants.TopicConstant.CREATE_CUSTOMER_NOTIFICATION;

@Configuration
@Profile({"dev"})
class KafkaConfig {

    @Bean
    public NewTopic generateAccountTopic() {
        Map<String, String> config = new HashMap<>();
        config.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE);
        config.put(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(604800000));

        return TopicBuilder
                .name(CREATE_CUSTOMER_NOTIFICATION)
                .replicas(1)
                .configs(config)
                .build();
    }
}
