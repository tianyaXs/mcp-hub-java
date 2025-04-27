package com.callisto.mcp.weather.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OpenMeteoService {

    private final WebClient webClient;

    public OpenMeteoService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.open-meteo.com/v1")
                .build();
    }

    @Tool(description = "根据经纬度获取天气预报")
    public String getWeatherForecastByLocation(
            @ToolParam(description = "纬度，例如：39.9042") String latitude,
            @ToolParam(description = "经度，例如：116.4074") String longitude) {

        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", "temperature_2m,wind_speed_10m")
                            .queryParam("timezone", "auto")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 解析响应并返回格式化的天气信息
            return "当前位置（纬度：" + latitude + "，经度：" + longitude + "）的天气信息：\n" + response;
        } catch (Exception e) {
            return "获取天气信息失败：" + e.getMessage();
        }
    }

    @Tool(description = "根据经纬度获取空气质量信息")
    public String getAirQuality(
            @ToolParam(description = "纬度，例如：39.9042") String latitude,
            @ToolParam(description = "经度，例如：116.4074") String longitude) {

        // 模拟数据，实际应用中应调用真实API
        return "当前位置（纬度：" + latitude + "，经度：" + longitude + "）的空气质量：\n" +
                "- PM2.5: 15 μg/m³ (优)\n" +
                "- PM10: 28 μg/m³ (良)\n" +
                "- 空气质量指数(AQI): 42 (优)\n" +
                "- 主要污染物: 无";
    }

}
