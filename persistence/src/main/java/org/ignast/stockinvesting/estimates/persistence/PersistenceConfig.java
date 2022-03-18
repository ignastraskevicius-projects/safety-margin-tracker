package org.ignast.stockinvesting.estimates.persistence;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "org.ignast.stockinvesting.estimates.service")
@EntityScan("org.ignast.stockinvesting.estimates.domain")
public class PersistenceConfig {}
