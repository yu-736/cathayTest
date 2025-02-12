package com.example.cathayTest.bean.res;

import lombok.Data;

import java.io.Serializable;

/**
 * 幣別匯率資料 Response
 */
@Data
public class CurrencyResModelBean implements Serializable {
    /* 幣別代碼 */
    private String code;

    /* 貨幣符號 */
    private String symbol;

    /* 匯率 */
    private String rate;

    /* 描述 */
    private String description;

    /* 匯率 */
    private float rate_float;

    /* 幣別中文名稱 */
    private String CurrencyNm;
}
