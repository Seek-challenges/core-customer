package pe.seek.core.config.broker.sqs;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Slf4j
@Component
@Profile({"prod"})
@RequiredArgsConstructor
class SqsClientShutdown {

    private final SqsAsyncClient sqsAsyncClient;

    @PreDestroy
    public void shutdown() {
        log.info("🔻 Cerrando SqsAsyncClient...");
        try {
            sqsAsyncClient.close();
            log.info("✅ SqsAsyncClient cerrado correctamente");
        } catch (Exception e) {
            log.error("❌ Error al cerrar SqsAsyncClient", e);
        }
    }
}