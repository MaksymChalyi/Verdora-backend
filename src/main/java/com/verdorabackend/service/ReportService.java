package com.verdorabackend.service;

import com.verdorabackend.dto.response.PendingPaymentOrderResponse;
import com.verdorabackend.dto.response.RevenueReportResponse;
import com.verdorabackend.dto.response.TopCancelledProductResponse;
import com.verdorabackend.dto.response.TopPurchasedProductResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<TopPurchasedProductResponse> getTopPurchasedProducts();

    List<TopCancelledProductResponse> getTopCancelledProducts();

    List<PendingPaymentOrderResponse> getPendingPaymentOrders(int days);

    List<RevenueReportResponse> getRevenueReport(LocalDate dateFrom, LocalDate dateTo, String groupBy);
}
