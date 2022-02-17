package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanyJsonBodyFactoryTest {

    private CompanyJsonBodyFactory factory = new CompanyJsonBodyFactory();

    @Test
    public void shouldCreateValidJson() {
        assertThat(factory.createAmazon()).isEqualTo(
                "{\"name\":\"Amazon\",\"address\":{\"country\":\"United States\"},\"listings\":\"listings\"}");
    }

    @Test
    public void shouldCreateCompanyNoName() {
        assertThat(factory.createWithNameJsonPair(""))
                .isEqualTo("{\"address\":{\"country\":\"United States\"},\"listings\":\"listings\"}");
    }

    @Test
    public void shouldCreateCompanyCustomNameJsonPair() {
        assertThat(factory.createWithNameJsonPair("\"name\":null"))
                .isEqualTo("{\"name\":null,\"address\":{\"country\":\"United States\"},\"listings\":\"listings\"}");
    }

    @Test
    public void shouldCreateCompanyNoAddress() {
        assertThat(factory.createWithAddressJsonPair("")).isEqualTo("{\"name\":\"Amazon\",\"listings\":\"listings\"}");
    }

    @Test
    public void shouldCreateCompanyCustomAddressJsonPair() {
        assertThat(factory.createWithAddressJsonPair("\"address\":null"))
                .isEqualTo("{\"name\":\"Amazon\",\"address\":null,\"listings\":\"listings\"}");
    }
}
