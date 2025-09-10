package com.example.second;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OSRMService {
    @GET("route/v1/driving/{startLon},{startLat};{endLon},{endLat}")
    Call<OSRMResponse> getRoute(
            @Path("startLon") double startLon,
            @Path("startLat") double startLat,
            @Path("endLon") double endLon,
            @Path("endLat") double endLat,
            @Query("overview") String overview
    );
}
