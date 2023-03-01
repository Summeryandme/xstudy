package com.xstudy.media.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {
  @Bean
  public Executor videoProcessExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    int processNum = Runtime.getRuntime().availableProcessors();
    int corePoolSize = (int) (processNum / (1 - 0.2));
    int maxPoolSize = (int) (processNum / (1 - 0.5));
    taskExecutor.setCorePoolSize(corePoolSize);
    taskExecutor.setMaxPoolSize(maxPoolSize);
    taskExecutor.setQueueCapacity(maxPoolSize * 1000);
    taskExecutor.setKeepAliveSeconds(60);
    taskExecutor.setThreadNamePrefix("video-process--");
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    taskExecutor.setAwaitTerminationSeconds(60);
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    return taskExecutor;
  }
}
