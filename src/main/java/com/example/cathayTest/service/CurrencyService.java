package com.example.cathayTest.service;

import com.example.cathayTest.bean.req.CurrencyReqModelBean;
import com.example.cathayTest.bean.res.CoinDeskResModelBean;
import com.example.cathayTest.entity.CurrencyEntity;
import com.example.cathayTest.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 幣別資料維護 Service
 */
@Service
public class CurrencyService {
    /* 幣別資料 Repository */
    @Autowired
    private CurrencyRepository currencyRepository;

    private static final String BPI_URL = "https://api.coinDesk.com/v1/bpi/currentprice.json";

    /**
     * 取得CoinDesk幣別資料
     * @param responseType 回傳型別
     * @return 所有幣別資料
     */
    public <T> T doGetAllDate(Class<T> responseType) {
        return new RestTemplate().getForObject(BPI_URL, responseType);
    }

    /**
     * 取得CoinDesk轉換後的幣別資料
     * @return CoinDesk所有轉換後的幣別資料
     */
    @Transactional(readOnly = true)
    public CoinDeskResModelBean doGetAllConverseDate() {
        // 1. 取得幣別資料
        CoinDeskResModelBean CoinDeskResModelBean = doGetAllDate(CoinDeskResModelBean.class);

        // 2. 轉換時間格式
        String updatedISO = OffsetDateTime.parse(CoinDeskResModelBean.getTime().getUpdatedISO(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        CoinDeskResModelBean.getTime().setUpdatedISO(updatedISO);

        String update = dateTimeFormat(CoinDeskResModelBean.getTime().getUpdated(), "MMM d, yyyy HH:mm:ss z");
        CoinDeskResModelBean.getTime().setUpdated(update);

        String updateuk = dateTimeFormat(CoinDeskResModelBean.getTime().getUpdateduk().replace("at", ""), "MMM d, yyyy HH:mm z");
        CoinDeskResModelBean.getTime().setUpdateduk(updateuk);

        // 3. 更新幣別中文名稱欄位
        CoinDeskResModelBean.getBpi().entrySet().forEach(map -> {
            if (isPresent(map.getKey())) {
                map.getValue().setCurrencyNm(getCurrencyById(map.getKey()).getCurrencyNm());
            }
        });

        return CoinDeskResModelBean;
    }

    /**
     * 查詢幣別資料是否存在
     * @param currency 幣別代碼
     * @return boolean 是否存在
     */
    @Transactional(readOnly = true)
    public boolean isPresent(String currency) {
        return currencyRepository.findById(currency).isPresent();
    }

    /**
     * 新增幣別資料
     * @param currency 持行參數
     * @return 新增結果
     */
    @Transactional
    public CurrencyEntity addCurrency(CurrencyEntity currency) {
        return currencyRepository.save(currency);
    }

    /**
     * 根據幣別代碼取得幣別資訊
     * @param currency 執行參數
     * @return 幣別資訊
     */
    @Transactional(readOnly = true)
    public CurrencyEntity getCurrencyById(String currency) {
        return currencyRepository.findById(currency).orElse(null);
    }

    /**
     * 更新幣別資料
     * @param currency 執行參數
     * @return 更新結果
     */
    @Transactional
    public CurrencyEntity updCurrencyNm(CurrencyReqModelBean currency) {
        return currencyRepository.save(new CurrencyEntity(currency.getCurrency(), currency.getCurrencyNm()));
    }

    /**
     * 刪除幣別資料
     * @param currency 執行參數
     * @return 是否執行成功
     */
    @Transactional
    public boolean delCurrency(String currency) {
        currencyRepository.deleteById(currency);

        return !isPresent(currency);
    }

    /**
     * 時間格式轉換
     * @param dateTime 時間
     * @param originalPattern 時間原始格式
     * @return 時間 (yyyy/MM/dd HH:mm:ss)
     */
    private String dateTimeFormat(String dateTime, String originalPattern) {
        // 1. 取得時區
        String timeZone = dateTime.substring(dateTime.length() - 3);

        // 2. 指定日期格式並設定時區
        SimpleDateFormat format = new SimpleDateFormat(originalPattern, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone(timeZone));

        // 3. 執行格式轉換
        try {
            Date date = format.parse(dateTime);
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
        } catch (ParseException e) {
            return dateTime;
        }
    }
}
