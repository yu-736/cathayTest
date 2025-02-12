-------------------------------------
-- Table DDL                       --
-------------------------------------

-- 幣別資料表
CREATE TABLE IF NOT EXISTS CURRENCY (
    CURRENCY VARCHAR(3) PRIMARY KEY,
    CURRENCY_NM VARCHAR(50)
);