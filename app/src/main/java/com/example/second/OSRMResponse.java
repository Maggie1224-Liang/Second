package com.example.second;

import java.util.List;

public class OSRMResponse {
    public List<Route> routes;

    public static class Route {
        public double distance; // 公尺
        public double duration; // 秒
    }
}
