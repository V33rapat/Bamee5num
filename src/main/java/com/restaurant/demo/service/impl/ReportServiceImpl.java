package com.restaurant.demo.service.impl;

import com.restaurant.demo.dto.ReportSummary;
import com.restaurant.demo.service.ReportService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ReportSummary getMonthlyReport(Integer month, Integer year) {
        if (year == null) {
            year = java.time.LocalDate.now().getYear();
        }

        // ถ้าเลือก “ทั้งปี” (month == null หรือ 0)
        boolean isWholeYear = (month == null || month == 0);

        // 🔹 1. ยอดขายรวม
        String totalRevenueSql = """
            SELECT COALESCE(SUM(o.total_amount), 0)
            FROM orders o
            WHERE o.status = 'FINISH'
            AND YEAR(o.created_at) = :year
            """ + (isWholeYear ? "" : " AND MONTH(o.created_at) = :month");

        var totalRevenueQuery = entityManager.createNativeQuery(totalRevenueSql)
                .setParameter("year", year);
        if (!isWholeYear) totalRevenueQuery.setParameter("month", month);
        BigDecimal totalRevenue = (BigDecimal) totalRevenueQuery.getSingleResult();

        // 🔹 2. จำนวนออเดอร์ทั้งหมด
        String totalOrdersSql = """
            SELECT COUNT(*)
            FROM orders o
            WHERE o.status = 'FINISH'
            AND YEAR(o.created_at) = :year
            """ + (isWholeYear ? "" : " AND MONTH(o.created_at) = :month");

        var totalOrdersQuery = entityManager.createNativeQuery(totalOrdersSql)
                .setParameter("year", year);
        if (!isWholeYear) totalOrdersQuery.setParameter("month", month);
        long totalOrders = ((Number) totalOrdersQuery.getSingleResult()).longValue();

        // 🔹 3. เมนูขายดีที่สุด
        String topMenuSql = """
            SELECT oi.item_name, SUM(oi.quantity) AS total_sold
            FROM order_items oi
            JOIN orders o ON oi.order_id = o.id
            WHERE o.status = 'FINISH'
            AND YEAR(o.created_at) = :year
            """ + (isWholeYear ? "" : " AND MONTH(o.created_at) = :month") + """
            GROUP BY oi.item_name
            ORDER BY total_sold DESC
            LIMIT 1
        """;

        var topMenuQuery = entityManager.createNativeQuery(topMenuSql)
                .setParameter("year", year);
        if (!isWholeYear) topMenuQuery.setParameter("month", month);

        String topMenu = "-";
        long topCount = 0;
        List<Object[]> topResult = topMenuQuery.getResultList();
        if (!topResult.isEmpty()) {
            Object[] row = topResult.get(0);
            topMenu = Optional.ofNullable(row[0]).map(Object::toString).orElse("-");
            topCount = ((Number) row[1]).longValue();
        }

        // 🔹 4. ยอดขายรายเดือน (เพื่อใช้ทำกราฟ)
        String monthlySalesSql = """
            SELECT MONTH(o.created_at), COALESCE(SUM(o.total_amount), 0)
            FROM orders o
            WHERE o.status = 'FINISH'
            AND YEAR(o.created_at) = :year
            GROUP BY MONTH(o.created_at)
            ORDER BY MONTH(o.created_at)
        """;

        var monthlySalesQuery = entityManager.createNativeQuery(monthlySalesSql)
                .setParameter("year", year);
        List<Object[]> monthlySalesResult = monthlySalesQuery.getResultList();

        List<BigDecimal> monthlySales = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            BigDecimal value = BigDecimal.ZERO;
            for (Object[] row : monthlySalesResult) {
                int m = ((Number) row[0]).intValue();
                if (m == i) {
                    value = (BigDecimal) row[1];
                    break;
                }
            }
            monthlySales.add(value);
        }

        // 🔹 รวมข้อมูลทั้งหมดลงใน DTO
        return new ReportSummary(totalRevenue, totalOrders, topMenu, topCount, monthlySales);
    }
}
