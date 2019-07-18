package com.iscas.cs.server.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/4 21:59
 * @since jdk1.8
 */
@Slf4j
public class SimpleQuartzJob implements Job {
    private JobExecutor jobExecutor;
    public SimpleQuartzJob() {
        jobExecutor = JobExecutor.getInstance();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("In SimpleQuartzJob - executing its JOB at "
                + new Date() + " by " + context.getTrigger().getName());
        jobExecutor.execute();
    }
}
