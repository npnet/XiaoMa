package com.xiaoma.assistant.model;

import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2019/3/12
 */
public class GasPriceBean {

    private List<GasPrice> oil;

    public List<GasPrice> getGasPrices() {
        return oil;
    }

    public void setGasPrices(List<GasPrice> gasPrices) {
        this.oil = gasPrices;
    }

    public static class GasPrice {
        private String name;
        private String price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getGasType() {
            return name;
        }

        public String getGasPrice() {
            return price;
        }
    }
}
