package com.restaurant.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public class ReportSummary {
    private BigDecimal totalRevenue;  // ยอดขายรวม
    private long totalOrders;         // จำนวนออเดอร์ทั้งหมด
    private String topMenu;           // เมนูขายดีที่สุด
    private long topCount;            // จำนวนที่ขายได้ของเมนูนั้น
    private List<BigDecimal> monthlySales; // ยอดขายแต่ละเดือน (ใช้กับกราฟ)

    public ReportSummary() {}

    public ReportSummary(BigDecimal totalRevenue, long totalOrders, String topMenu, long topCount, List<BigDecimal> monthlySales) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.topMenu = topMenu;
        this.topCount = topCount;
        this.monthlySales = monthlySales;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public String getTopMenu() {
        return topMenu;
    }

    public void setTopMenu(String topMenu) {
        this.topMenu = topMenu;
    }

    public long getTopCount() {
        return topCount;
    }

    public void setTopCount(long topCount) {
        this.topCount = topCount;
    }

    public List<BigDecimal> getMonthlySales() {
        return monthlySales;
    }

    public void setMonthlySales(List<BigDecimal> monthlySales) {
        this.monthlySales = monthlySales;
    }
}

