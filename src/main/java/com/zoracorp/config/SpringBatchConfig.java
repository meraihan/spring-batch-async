package com.zoracorp.config;

import com.zoracorp.listener.ItemCountListener;
import com.zoracorp.model.Users;
import com.zoracorp.service.UserService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;
    @Autowired
    public UserService userService;
    @Value("${file.path}")
    private String filePath;

    @Bean
    public FlatFileItemReader<Users> reader() {
        FlatFileItemReader<Users> reader = new FlatFileItemReader<Users>();
        reader.setResource(new FileSystemResource(filePath));

        reader.setLineMapper(new DefaultLineMapper<Users>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "firstname", "phone"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper() {{
                setTargetType(Users.class);
            }});
        }});
        return reader;
    }

    @Bean
    public AsyncItemProcessor asyncItemProcessor() throws Exception{
        AsyncItemProcessor<Users, Users> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(itemProcessor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        asyncItemProcessor.afterPropertiesSet();
        return asyncItemProcessor;
    }

    @Bean
    public ItemProcessor itemProcessor(){
        return new ItemProcessor<Users, Users>() {

            @Override
            public Users process(Users item) throws Exception {
                Thread.sleep(new Random().nextInt(100));
                return Users.builder().firstname(item.getFirstname())
                        .phone(item.getPhone()).build();
            }
        };
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(1000);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("TreeWayExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Job importUserJob(JobBuilderFactory jobs, Step s1, JobExecutionListener listener) {
        return jobs.get("UploadUserData")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(s1)
                .end()
                .build();
    }

    @SuppressWarnings("unchecked")
    @Bean
    public Step step1() throws Exception {
        return stepBuilderFactory.get("step1")
                .chunk(50)
                .reader(reader())
                .processor(asyncItemProcessor())
                .writer(asyncItemWriter())
                .listener(listener())
                .build();
    }


    @Bean
    public JdbcBatchItemWriter<Users> usersItemWriter() {
        JdbcBatchItemWriter<Users> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("INSERT INTO users (firstname,phone) VALUES (:firstname, :phone)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public AsyncItemWriter<Users> asyncItemWriter() throws Exception{
        AsyncItemWriter<Users> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(usersItemWriter());
        asyncItemWriter.afterPropertiesSet();
        return asyncItemWriter;
    }

    @Bean
    public ItemCountListener listener() {
        return new ItemCountListener();
    }
}
