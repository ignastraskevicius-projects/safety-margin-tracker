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
    public void shouldCreateCompanyWithoutName() {
        assertThat(factory.createWithNameJsonPair(""))
                .isEqualTo("{\"address\":{\"country\":\"United States\"},\"listings\":\"listings\"}");
    }

    @Test
    public void shouldCreateCompanyWithCustomNameJsonPair() {
        assertThat(factory.createWithNameJsonPair("\"name\":null"))
                .isEqualTo("{\"name\":null,\"address\":{\"country\":\"United States\"},\"listings\":\"listings\"}");
    }

    @Test
    public void shouldCreateCompanyWithoutAddress() {
        assertThat(factory.createWithAddressJsonPair("")).isEqualTo("{\"name\":\"Amazon\",\"listings\":\"listings\"}");
    }

    @Test
    public void shouldCreateCompanyWithCustomAddressJsonPair() {
        assertThat(factory.createWithAddressJsonPair("\"address\":null"))
                .isEqualTo("{\"name\":\"Amazon\",\"address\":null,\"listings\":\"listings\"}");
    }

    @Test
    public void shouldCreateCompanyWithoutCountry() {
        assertThat(factory.createWithCountryJsonPair(""))
                .isEqualTo("{\"name\":\"Amazon\",\"address\":{},\"listings\":\"listings\"}");
    }

    @Test
    public void shouldCreateCompanyWithCustomCountryJsonPair() {
        assertThat(factory.createWithCountryJsonPair("\"country\":null"))
                .isEqualTo("{\"name\":\"Amazon\",\"address\":{\"country\":null},\"listings\":\"listings\"}");
    }
}