package com.example.cathayTest.ctrl;

import com.example.cathayTest.bean.req.CurrencyReqModelBean;
import com.example.cathayTest.bean.res.CoinDeskResModelBean;
import com.example.cathayTest.entity.CurrencyEntity;
import com.example.cathayTest.service.CurrencyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 幣別資料維護 Controller
 */
@RestController
@RequestMapping("/coinDesk")
public class CoinDeskController {
    /* 幣別資料維護 Service */
    @Autowired
    private CurrencyService currencyService;

    /**
     * 取得CoinDesk幣別資料
     * @return 幣別資料
     */
    @GetMapping("/all")
    public Object doGetAllDate() {
        return currencyService.doGetAllDate(Object.class);
    }

    /**
     * 取得CoinDesk轉換後的幣別資料
     * @return 轉換後的幣別資料
     */
    @GetMapping("/all/converse")
    public CoinDeskResModelBean doGetAllConverseDate() {
        return currencyService.doGetAllConverseDate();
    }

    /**
     * 新增幣別資料
     * @param currency 執行參數
     * @return 新增結果
     */
    @PostMapping("/add")
    public CurrencyEntity doAddCurrency(@Valid @RequestBody CurrencyReqModelBean currency) {
        return currencyService.addCurrency(currency);
    }

    /**
     * 根據幣別代碼取得幣別資訊
     * @param currency 執行參數
     * @return 幣別資訊
     */
    @GetMapping("/currency/{currency}")
    public ResponseEntity<CurrencyEntity> getCurrencyById(@PathVariable String currency) {
        //  1. 檢核執行參數
        if (StringUtils.isBlank(currency)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if(!currencyService.isPresent(currency)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // 2. 查詢幣別資訊
        return ResponseEntity.status(HttpStatus.OK)
                .body(currencyService.getCurrencyById(currency));
    }

    /**
     * 更新幣別資料
     * @param currency 執行參數
     * @param currencyNm 執行參數
     * @return 更新結果
     */
    @PutMapping("/update/{currency}/{currencyNm}")
    public ResponseEntity<CurrencyEntity> doUpdCurrency(@PathVariable String currency, @PathVariable String currencyNm) {
        // 1. 檢核執行參數
        if (StringUtils.isBlank(currencyNm)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if(!currencyService.isPresent(currency)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // 2. 執行更新幣別資料
        return ResponseEntity.status(HttpStatus.OK)
                .body(currencyService.updCurrencyNm(new CurrencyReqModelBean(currency, currencyNm)));
    }

    /**
     * 刪除幣別資料
     * @param currency 執行參數
     * @return 是否刪除成功
     */
    @DeleteMapping("/delete/{currency}")
    public boolean doDelCurrency(@PathVariable String currency) {
        // 1. 檢核資料是否存在
        if (!currencyService.isPresent(currency)) {
            return true;
        }

        // 2. 執行刪除幣別資料
        return currencyService.delCurrency(currency);
    }
}
