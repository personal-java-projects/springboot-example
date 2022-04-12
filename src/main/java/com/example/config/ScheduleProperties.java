package com.example.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ScheduleProperties {

    @Value("${schedule.pool-size}")
    private int poolSize;

    @Value("${schedule.remove-on-cancel-policy}")
    private Boolean removeOnCancelPolicy;

    @Value("${schedule.thread-name-prefix}")
    private String threadNamePrefix;

    @Value("${schedule.thread-group-name}")
    private String threadGroupName;
}
