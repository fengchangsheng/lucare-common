package com.fcs.common.excel.util;

import com.fcs.common.constant.CommonContant;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by fengcs on 2018/1/6.
 */
public class ExcelFormat {

    /**
     * 格式化百分比
     * @param x
     * @param y
     * @return
     */
    public static String getPercent(double x, double y) {
        double percentX = x * 1.0;
        double percentY = y * 1.0;
        double percent = percentX / percentY;
        if(x == 0 || y == 0){
            percent = 0;
        }
        DecimalFormat df = new DecimalFormat("##0.00%");
        return df.format(percent);
    }


    public static String getPercent(double per) {
        if (per == 0){
            return "0.00%";
        }
        DecimalFormat df = new DecimalFormat("##0.00%");
        return df.format(per);
    }

    /**
     * 将分转化为元，保留2位小数
     * @param b
     * @return
     */
    public static BigDecimal converCentToYuan(BigDecimal b) {
        if(null == b){
            return CommonContant.BIG_DECIMAL_0;
        }
        return b.divide(CommonContant.BIG_DECIMAL_100, 2, BigDecimal.ROUND_HALF_UP);
    }

}
