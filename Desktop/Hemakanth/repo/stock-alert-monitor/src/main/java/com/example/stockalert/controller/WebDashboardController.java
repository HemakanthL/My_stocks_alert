package com.example.stockalert.controller;

import com.example.stockalert.config.DataSourceProperties;
import com.example.stockalert.dto.AlertRequest;
import com.example.stockalert.dto.AlertResponse;
import com.example.stockalert.dto.DataSourceSettings;
import com.example.stockalert.service.StockAlertService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WebDashboardController {

    private final StockAlertService stockAlertService;
    private final DataSourceProperties dataSourceProperties;

    public WebDashboardController(StockAlertService stockAlertService, DataSourceProperties dataSourceProperties) {
        this.stockAlertService = stockAlertService;
        this.dataSourceProperties = dataSourceProperties;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        List<AlertResponse> alerts = stockAlertService.getAllAlerts().stream()
                .map(AlertResponse::fromEntity)
                .toList();

        List<AlertResponse> triggered = stockAlertService.getTriggeredAlerts().stream()
                .map(AlertResponse::fromEntity)
                .toList();

        model.addAttribute("alerts", alerts);
        model.addAttribute("triggeredAlerts", triggered);
        model.addAttribute("newAlert", new AlertRequest("", null, null, true));
        model.addAttribute("dataSourceSettings", new DataSourceSettings(
                dataSourceProperties.getProvider(),
                "",
                dataSourceProperties.getBaseUrl()
        ));
        model.addAttribute("apiKeyConfigured",
                dataSourceProperties.getApiKey() != null && !dataSourceProperties.getApiKey().isBlank());
        return "dashboard";
    }

    @PostMapping("/alerts/create")
    public String createAlert(@ModelAttribute("newAlert") AlertRequest request) {
        stockAlertService.createAlert(request);
        return "redirect:/";
    }

    @PostMapping("/alerts/refresh")
    public String refreshAlerts(RedirectAttributes redirectAttributes) {
        try {
            stockAlertService.refreshAlerts();
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("refreshError", exception.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/datasource")
    public String updateDataSource(@ModelAttribute("dataSourceSettings") DataSourceSettings settings) {
        dataSourceProperties.setProvider(settings.provider());
        if (settings.apiKey() != null && !settings.apiKey().isBlank()) {
            dataSourceProperties.setApiKey(settings.apiKey());
        }
        dataSourceProperties.setBaseUrl(settings.baseUrl());
        return "redirect:/";
    }

    @PostMapping("/alerts/{id}/delete")
    public String deleteAlert(@PathVariable Long id) {
        stockAlertService.deleteAlert(id);
        return "redirect:/";
    }
}
