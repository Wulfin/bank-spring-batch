package org.id.bankspringbatch;

import org.id.bankspringbatch.processor.BankTransactionItemAnalyticsProcessor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BankRestController {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;
    @Autowired
    private BankTransactionItemAnalyticsProcessor analyticsProcessor;


    @GetMapping("startjob")
    public BatchStatus load() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                .addJobParameter("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        while (jobExecution.isRunning()){
            System.out.println("....");
        }

        return jobExecution.getStatus();
    }
    @GetMapping("analytics")
    public Map<String,Double> analytics(){
        Map<String,Double> map = new HashMap<>();
        map.put("totalCredit", analyticsProcessor.getTotalCredit());
        map.put("totalDebit", analyticsProcessor.getTotalDebit());
        return map;
    }

}
