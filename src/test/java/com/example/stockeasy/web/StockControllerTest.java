package com.example.stockeasy.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.service.MarketDataService;
import com.example.stockeasy.service.StockService;
import com.example.stockeasy.service.UserService;

@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @MockBean
    private MarketDataService marketDataService;

    @MockBean
    private UserService userService;

    private List<Stock> mockStocks;

    @BeforeEach
    void setUp() {
        Stock s1 = new Stock();
        s1.setSymbol("AAPL");
        s1.setCompanyName("Apple Inc.");
        s1.setSector("Technology");
        s1.setCurrentPrice(BigDecimal.valueOf(150.00));

        Stock s2 = new Stock();
        s2.setSymbol("JPM");
        s2.setCompanyName("JPMorgan Chase");
        s2.setSector("Financial Services");
        s2.setCurrentPrice(BigDecimal.valueOf(140.00));

        Stock s3 = new Stock();
        s3.setSymbol("KO");
        s3.setCompanyName("Coca-Cola");
        s3.setSector("Consumer Defensive");
        s3.setCurrentPrice(BigDecimal.valueOf(60.00));

        Stock s4 = new Stock();
        s4.setSymbol("XOM");
        s4.setCompanyName("Exxon Mobil");
        s4.setSector("Energy");
        s4.setCurrentPrice(BigDecimal.valueOf(105.00));

        mockStocks = Arrays.asList(s1, s2, s3, s4);
    }

    @Test
    @WithMockUser
    void testGetAllStocks_NoFilters() throws Exception {
        when(stockService.getActiveStocks()).thenReturn(mockStocks);

        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(view().name("stocks/list"))
                .andExpect(model().attribute("stocks", mockStocks));
    }

    @Test
    @WithMockUser
    void testGetAllStocks_SearchFilter() throws Exception {
        when(stockService.getActiveStocks()).thenReturn(mockStocks);

        mockMvc.perform(get("/stocks").param("search", "apple"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("stocks", List.of(mockStocks.get(0))));

        mockMvc.perform(get("/stocks").param("search", "JPM"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("stocks", List.of(mockStocks.get(1))));
    }

    @Test
    @WithMockUser
    void testGetAllStocks_SectorFilter_Finance() throws Exception {
        when(stockService.getActiveStocks()).thenReturn(mockStocks);

        // "Finance" should match "Financial Services"
        mockMvc.perform(get("/stocks").param("sector", "Finance"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("stocks", List.of(mockStocks.get(1))));
    }

    @Test
    @WithMockUser
    void testGetAllStocks_SectorFilter_Consumer() throws Exception {
        when(stockService.getActiveStocks()).thenReturn(mockStocks);

        // "Consumer" should match "Consumer Defensive"
        mockMvc.perform(get("/stocks").param("sector", "Consumer"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("stocks", List.of(mockStocks.get(2))));
    }

    @Test
    @WithMockUser
    void testGetAllStocks_PriceRangeFilter() throws Exception {
        when(stockService.getActiveStocks()).thenReturn(mockStocks);

        // 100-200 range (AAPL 150, JPM 140, XOM 105)
        mockMvc.perform(get("/stocks").param("priceRange", "100-200"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("stocks",
                        Arrays.asList(mockStocks.get(0), mockStocks.get(1), mockStocks.get(3))));
    }
}
