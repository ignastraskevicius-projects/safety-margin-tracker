package org.ignast.stockinvesting.quotes.persistence;

import lombok.val;
import org.h2.tools.Server;
import org.hibernate.cfg.AvailableSettings;
import org.ignast.stockinvesting.quotes.*;
import org.ignast.stockinvesting.quotes.dbmigration.ProductionDatabaseMigrationVersions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.transaction.TestTransaction;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.cfg.AvailableSettings.BEAN_CONTAINER;
import static org.ignast.stockinvesting.quotes.persistence.DomainFactoryForTests.*;
import static org.mockito.Mockito.mock;

@DataJpaTest
class CompanyPersistenceTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final CompanyName name = new CompanyName("Amazon");

    private static final UUID id = UUID.fromString("ad51aced-67a7-4c06-af35-23565517fccc");

    private static final StockSymbol symbol = new StockSymbol("AMZN");

    @Test
    public void shouldInsertCompany() {
        val company = new Company(id, new CompanyName("Amazon"), new StockSymbol("AMZN"), new StockExchanges(mock(QuotesRepository.class)).getFor(anyMIC()));
        commiting(() -> {
            companyRepository.save(company);
        });

        val result = jdbcTemplate.queryForMap("SELECT id FROM company;");

        assertThat(result.get("id")).isEqualTo(id.toString());
    }

    @Test
    public void shouldRetrieveCompany() {

    }

    private void commiting(Runnable runnable) {
        TestTransaction.flagForCommit();
        runnable.run();
        TestTransaction.end();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean(initMethod = "start", destroyMethod = "stop")
        public Server h2Server() throws SQLException {
            return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9093");
        }

        @Bean
        public StockExchanges stockExchanges() {
            return new StockExchanges(mock(QuotesRepository.class));
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, ConfigurableListableBeanFactory beanFactory) {
            val emFactory = new LocalContainerEntityManagerFactoryBean();
            HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
            Properties props = new Properties();
            props.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            emFactory.setPackagesToScan("org.ignast.stockinvesting.quotes.persistence");
            emFactory.setJpaProperties(props);
            emFactory.setDataSource(dataSource);
            emFactory.setJpaVendorAdapter(jpaVendorAdapter);
            emFactory.setPersistenceUnitName("quotes");
            emFactory.getJpaPropertyMap().put(BEAN_CONTAINER, new SpringBeanContainer(beanFactory));
            return emFactory;
        }

        @Bean
        public FlywayConfigurationCustomizer migratingToTargetVersion() {
            return c -> c.target(ProductionDatabaseMigrationVersions.TARGET);
        }
    }
}