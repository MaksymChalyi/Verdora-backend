package com.verdorabackend.service;

import com.verdorabackend.dto.response.TopPurchasedProductResponse;

import java.util.List;

public interface ReportService {

    List<TopPurchasedProductResponse> getTopPurchasedProducts();
}
