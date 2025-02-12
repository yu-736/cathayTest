package com.example.cathayTest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 幣別資料表 Entity
 */
@Entity
@Table(name = "CURRENCY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyEntity implements Serializable {
    /* 幣別 */
    @Id
    @Column(name = "CURRENCY")
    private String currency;

    /* 幣別中文名稱 */
    @Column(name = "CURRENCY_NM")
    private String currencyNm;
}
