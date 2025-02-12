package com.example.cathayTest.bean.res;

import lombok.Data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 資料更新日期 Response
 */
@Data
public class TimeResModelBean implements Serializable {
    private String updated;

    private String updatedISO;

    private String updateduk;
}
