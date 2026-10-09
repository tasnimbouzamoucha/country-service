package com.countryservice.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.countryservice.demo.beans.Country;
import com.countryservice.demo.repositories.CountryRepository;
import com.countryservice.demo.services.CountryService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceMockitoTests {

	@Mock
	CountryRepository countryrep;

	@InjectMocks
	CountryService countryService;

	public List<Country> mycountries;

	@Test
	@Order(1)
	public void test_getAllCountries() {
		List<Country> mycountries = new ArrayList<Country>();
		mycountries.add(new Country(1,"India","Delhi"));
		mycountries.add(new Country(2,"USA","Washington"));

		when(countryrep.findAll()).thenReturn(mycountries);//Mocking
		assertEquals(2, countryService.getAllCountries().size());
	}

	@Test
	@Order(2)
	public void test_getCountryByID() {
		List<Country> mycountries = new ArrayList<Country>();
		mycountries.add(new Country(1,"India","Delhi"));
		mycountries.add(new Country(2,"USA","Washington"));

		int countryID=1;
		when(countryrep.findAll()).thenReturn(mycountries);//Mocking
		assertEquals(countryID, countryService.getCountryById(countryID).getIdCountry());
	}

	@Test
	@Order(3)
	public void test_getCountryByName() {
		List<Country> mycountries = new ArrayList<Country>();
		mycountries.add(new Country(1,"India","Delhi"));
		mycountries.add(new Country(2,"USA","Washington"));

		String countryName="USA";
		when(countryrep.findAll()).thenReturn(mycountries);//Mocking
		assertEquals(countryName, countryService.getCountryByName(countryName).getName());
	}

	@Test
	@Order(4)
	public void test_addCountry() {
		Country country = new Country(3,"France","Paris");
		when(countryrep.save(country)).thenReturn(country);//Mocking
		assertEquals(country, countryService.addCountry(country));
	}

	@Test
	@Order(5)
	public void test_updateCountry() {
		Country country = new Country(3,"Germany","Berlin");
		when(countryrep.save(country)).thenReturn(country);//Mocking
		assertEquals(country, countryService.updateCountry(country));
	}

	@Test
	@Order(6)
	public void test_deleteCountry() {
		Country country = new Country(3,"Germany","Berlin");
		countryService.deleteCountry(country);
		verify(countryrep,times(1)).delete(country);
	}
}