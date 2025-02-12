package com.example.cathayTest.bean.res;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * CoinDesk 匯率資訊 Response
 */
@Data
public class CoinDeskResModelBean implements Serializable {
    private TimeResModelBean time;

    private String disclaimer;

    private String chartName;

    private Map<String, CurrencyResModelBean> bpi;
}
