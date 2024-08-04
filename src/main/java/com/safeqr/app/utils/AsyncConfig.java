package com.safeqr.app.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Sets the number of core threads. These threads are always kept alive.
        executor.setCorePoolSize(2);

        // Sets the maximum number of threads that can be created by the pool.
        executor.setMaxPoolSize(2);

        // Sets the size of the queue to hold tasks before they are executed.
        executor.setQueueCapacity(500);

        // Sets the prefix for the names of the threads created by this pool.
        executor.setThreadNamePrefix("GmailProcessing-");

        // Initializes the executor to apply the configuration and make it ready to use.
        executor.initialize();

        return executor;
    }
}
