package org.id.bankspringbatch;

import org.id.bankspringbatch.dao.BankTransaction;
import org.id.bankspringbatch.dao.BankTransactionRepository;
import org.id.bankspringbatch.processor.BankTransactionItemAnalyticsProcessor;
import org.id.bankspringbatch.processor.BankTransactionItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
/*    @Autowired
    private ItemReader<BankTransaction> bankTransactionItemReader;
    @Autowired
    private ItemWriter<BankTransaction> bankTransactionItemWriter;
    @Autowired
    private ItemProcessor<BankTransaction, BankTransaction> bankTransactionItemProcessor;*/

    @Bean
    public Job bankJob(JobRepository jobRepository, Step step1) {
        /*Step step1 = stepBuilderFactory.get("step-load-data")
                .<BankTransaction, BankTransaction>chunk(100)
                .reader(bankTransactionItemReader)
                .processor(bankTransactionItemProcessor)
                .writer(bankTransactionItemWriter)
                .build();*/

        return new JobBuilder("bank-data-loader-job", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager,
                     ItemReader<BankTransaction> bankTransactionItemReader,
                     ItemProcessor <BankTransaction, BankTransaction> bankTransactionItemProcessor,
                     ItemWriter<BankTransaction> bankTransactionItemWriter){
        return new StepBuilder("step-load-data", jobRepository)
                .<BankTransaction, BankTransaction>chunk(100, transactionManager)
                .reader(bankTransactionItemReader)
                .processor(compositeItemProcessor())
                .writer(bankTransactionItemWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<BankTransaction> flatFileItemReader(@Value("${inputFile}") Resource inputFile){
        /*FlatFileItemReader<BankTransaction> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setName("csv-reader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;*/
        return new FlatFileItemReaderBuilder<BankTransaction>()
                .name("csv-reader")
                .linesToSkip(1)
                .lineMapper(lineMapper())
                .resource(inputFile)
                .build();
    }

    @Bean
    public LineMapper<BankTransaction> lineMapper(){
        DefaultLineMapper<BankTransaction> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "accountID", "strTransactionDate", "transactionType", "amount");
        lineMapper.setLineTokenizer(lineTokenizer);

        BeanWrapperFieldSetMapper<BankTransaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(BankTransaction.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public ItemProcessor<BankTransaction,BankTransaction> compositeItemProcessor() {
        List<ItemProcessor<BankTransaction,BankTransaction>> itemProcessors = new ArrayList<>();
        itemProcessors.add(bankTransactionItemProcessor());
        itemProcessors.add(bankTransactionItemAnalyticsProcessor());

        CompositeItemProcessor<BankTransaction,BankTransaction> compositeItemProcessor = new
                CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(itemProcessors);
        return compositeItemProcessor;
    }

    @Bean
    public BankTransactionItemProcessor bankTransactionItemProcessor(){
        return new BankTransactionItemProcessor();
    }
    @Bean
    public BankTransactionItemAnalyticsProcessor bankTransactionItemAnalyticsProcessor(){
        return new BankTransactionItemAnalyticsProcessor();
    }

    @Bean
    public ItemWriter<BankTransaction> itemWriter(){
        return new ItemWriter<BankTransaction>() {
            @Autowired
            private BankTransactionRepository bankTransactionRepository;
            @Override
            public void write(Chunk<? extends BankTransaction> chunk) throws Exception {
                bankTransactionRepository.saveAll(chunk);
            }
        };
    }

}
