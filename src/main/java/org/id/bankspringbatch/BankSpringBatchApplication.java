package org.id.bankspringbatch;

import org.id.bankspringbatch.processor.BankTransactionItemAnalyticsProcessor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BankSpringBatchApplication implements CommandLineRunner {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;
    @Autowired
    private BankTransactionItemAnalyticsProcessor analyticsProcessor;

    public static void main(String[] args) {
        SpringApplication.run(BankSpringBatchApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                .addJobParameter("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        while (jobExecution.isRunning()){
            System.out.println("....");
        }

        Map<String,Double> map = new HashMap<>();
        map.put("totalCredit", analyticsProcessor.getTotalCredit());
        map.put("totalDebit", analyticsProcessor.getTotalDebit());

        System.out.println(map);
        System.out.println(jobExecution.getStatus());
    }
}
