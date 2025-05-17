package pe.seek.core.config.async;

import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;

import static pe.seek.core.shared.constants.AsyncConstants.ASYNC_VIRTUAL_THREAD_TASK_EXECUTOR;

@Configuration
class AsyncConfig {

    @Primary
    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(1000);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Async-");
        executor.setTaskDecorator(new MDCTaskDecoratorConfig());
        return executor;
    }

    @Bean(ASYNC_VIRTUAL_THREAD_TASK_EXECUTOR)
    public AsyncTaskExecutor asyncTaskExecutor() {
        TaskExecutorAdapter asyncTaskExecutor = new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
        asyncTaskExecutor.setTaskDecorator(new MDCTaskDecoratorConfig());
        return asyncTaskExecutor;
    }
}
