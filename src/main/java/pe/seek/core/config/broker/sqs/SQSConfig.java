package pe.seek.core.config.broker.sqs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@Profile({"prod"})
class SQSConfig {

    @Value("${aws.cloud.region}")
    private String region;

    @Bean
    public SqsAsyncClient sqsAsyncClient(AwsCredentialsProvider credential) {
        return SqsAsyncClient.builder()
            .credentialsProvider(credential)
            .region(Region.of(region))
            .build();
    }
}
