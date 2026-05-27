package com.example.demo.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {
    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .defaultSchema("public")
                .validateMigrationNaming(true)
                .validateOnMigrate(true)
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .load();

        // 🚀 This is the missing piece that forces Flyway to run on startup!
        flyway.migrate();

        return flyway;
    }
}
