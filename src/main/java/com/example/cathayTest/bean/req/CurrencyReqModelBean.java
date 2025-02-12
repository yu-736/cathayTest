package com.example.cathayTest.bean.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 幣別資料 Request
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyReqModelBean implements Serializable {
    /* 幣別 */
    @NotBlank
    @Size(max = 3)
    private String currency;

    /* 幣別中文名稱 */
    @NotBlank
    @Size(max = 50)
    private String currencyNm;
}
