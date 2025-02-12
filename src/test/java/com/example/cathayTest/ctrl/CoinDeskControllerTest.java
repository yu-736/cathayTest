package com.example.cathayTest.ctrl;

import com.example.cathayTest.bean.res.CoinDeskResModelBean;
import com.example.cathayTest.entity.CurrencyEntity;
import com.example.cathayTest.repository.CurrencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CoinDeskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // 用於反序列化 JSON

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    void doGetAllDate() throws Exception {
        mockMvc.perform(get("/coinDesk/all"))
                .andExpect(status().is(200));
    }

    @Test
    void doGetAllConverseDate() throws Exception {
        String response = mockMvc.perform(get("/coinDesk/all/converse"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);


        CoinDeskResModelBean currencyResponse = objectMapper.readValue(response, CoinDeskResModelBean.class);

        // 1. 驗證時間轉換格式
        assertTrue(isValidDate(currencyResponse.getTime().getUpdated()));
        assertTrue(isValidDate(currencyResponse.getTime().getUpdatedISO()));
        assertTrue(isValidDate(currencyResponse.getTime().getUpdateduk()));

        // 2. 驗證幣別中文轉換欄位
        currencyResponse.getBpi().entrySet().forEach(map -> {
            CurrencyEntity currencyEntity = currencyRepository.findById(map.getKey()).orElse(null);
            if (currencyEntity != null) {
                // 1.1 若幣別存在於對照表，則驗證幣別中文名稱
                assertEquals(map.getValue().getCurrencyNm(), currencyEntity.getCurrencyNm());
            } else {
                // 1.2 若幣別不存在於對照表，證幣別中文名稱須為空
                assertNull(map.getValue().getCurrencyNm());
            }
        });
    }

    @Test
    @Transactional
    void doAddCurrency() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/coinDesk/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"currency\": \"JPY\",\n" +
                        "    \"currencyNm\": \"日幣\"\n" +
                        "}");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency", equalTo("JPY")))
                .andExpect(jsonPath("$.currencyNm", equalTo("日幣")));
    }

    @Test
    void getCurrencyById() throws Exception {
        // 1. 測試已存在資料
        mockMvc.perform(get("/coinDesk/currency/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyNm", equalTo("美元")));

        // 2. 測試不存在資料
        mockMvc.perform(get("/coinDesk/currency/JPY"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void doUpdCurrency() throws Exception {
        // 1. 測試已存在資料
        mockMvc.perform(put("/coinDesk/update/USD/美金"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency", equalTo("USD")))
                .andExpect(jsonPath("$.currencyNm", equalTo("美金")));

        // 2. 測試不存在資料
        mockMvc.perform(put("/coinDesk/update/EUR/歐元"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void doDelCurrency() throws Exception {
        mockMvc.perform(delete("/coinDesk/delete/USD"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    private static boolean isValidDate(String date) {
        try {
            LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}