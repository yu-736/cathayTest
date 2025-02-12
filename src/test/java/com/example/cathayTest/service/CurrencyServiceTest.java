package com.example.cathayTest.service;

import com.example.cathayTest.bean.req.CurrencyReqModelBean;
import com.example.cathayTest.bean.res.CoinDeskResModelBean;
import com.example.cathayTest.entity.CurrencyEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CurrencyServiceTest {
    /* 幣別資料維護 Service */
    @Autowired
    private CurrencyService currencyService;

    @Test
    void doGetAllDate() {
        Object result = currencyService.doGetAllDate(Object.class);
        assertNotNull(result);
    }

    @Test
    void doGetAllConverseDate() {
        CoinDeskResModelBean result = currencyService.doGetAllConverseDate();

        // 1. 驗證幣別中文轉換欄位
        result.getBpi().entrySet().forEach(map -> {
            CurrencyEntity currencyEntity = currencyService.getCurrencyById(map.getKey());

            if (currencyEntity != null) {
                assertEquals(map.getValue().getCurrencyNm(), currencyEntity.getCurrencyNm());
            } else {
                assertNull(map.getValue().getCurrencyNm());
            }
        });

        // 2. 驗證時間轉換格式
        assertTrue(isValidDate(result.getTime().getUpdated()));
        assertTrue(isValidDate(result.getTime().getUpdatedISO()));
        assertTrue(isValidDate(result.getTime().getUpdateduk()));
    }

    @Test
    void isPresent() {
        assertTrue(currencyService.isPresent("USD"));
        assertFalse(currencyService.isPresent("EUR"));
    }

    @Test
    void getCurrencyById() {
        // 1. 測試已存在資料
        assertEquals("新台幣", currencyService.getCurrencyById("NTD").getCurrencyNm());

        // 2. 測試不存在資料
        assertNull(currencyService.getCurrencyById("JPY"));
    }

    @Test
    @Transactional
    void addCurrency() {
        CurrencyEntity result = currencyService.addCurrency(new CurrencyReqModelBean("EUR", "歐元"));

        assertEquals("EUR", result.getCurrency());
        assertEquals("歐元", result.getCurrencyNm());
    }

    @Test
    @Transactional
    void updCurrencyNm() {
        CurrencyEntity result = currencyService.updCurrencyNm(new CurrencyReqModelBean("NTD", "新臺幣"));

        assertEquals("新臺幣", result.getCurrencyNm());
    }

    @Test
    @Transactional
    void delCurrency() {
        // 1. 刪除已存在的資料
        currencyService.delCurrency("USD");
        assertFalse(currencyService.isPresent("USD"));

        // 2. 刪除不存在的資料
        assertThrows(EmptyResultDataAccessException.class, ()->{
            currencyService.delCurrency("JPY");
        });
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