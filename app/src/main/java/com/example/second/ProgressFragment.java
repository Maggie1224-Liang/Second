package com.example.second;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProgressFragment extends Fragment {

    private FusedLocationProviderClient fused;
    private TextView tvInfo;   // 顯示在「位置資訊」下方（地圖上方）

    // 目的地：高雄市岡山高醫
    private static final double HOSPITAL_LAT = 22.796408;
    private static final double HOSPITAL_LON = 120.297271;

    // 一次性定位 callback
    private final LocationCallback oneShotCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult result) {
            Location loc = result.getLastLocation();
            if (loc != null) {
                showLocationAndStartRoute(loc);
            } else {
                tvInfo.setText("❌ 抓不到目前位置，請再試一次。");
            }
            try { fused.removeLocationUpdates(this); } catch (Exception ignored) {}
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        // ---- 你的原本 UI ----
        Spinner spinnerDepartment = view.findViewById(R.id.spinner_department);
        spinnerDepartment.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"神經外科", "家醫科", "骨科"}));

        Spinner spinnerSession = view.findViewById(R.id.spinner_session);
        spinnerSession.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"上午診", "下午診"}));

        List<Doctor> doctorList = new ArrayList<>();
        doctorList.add(new Doctor("XXX", "---"));
        doctorList.add(new Doctor("YYY", "AAA"));
        doctorList.add(new Doctor("ZZZ", "BBB"));
        ViewPager2 pager = view.findViewById(R.id.viewPagerDoctor);
        pager.setAdapter(new DoctorPagerAdapter(doctorList));

        // ---- 定位 / 顯示 / 導航 ----
        fused = LocationServices.getFusedLocationProviderClient(requireContext());
        tvInfo = view.findViewById(R.id.tvInfo);

        ImageView mapImage = view.findViewById(R.id.image_map_placeholder);
        mapImage.setOnClickListener(v -> openGoogleMapsNav());

        Button btnNavigate = view.findViewById(R.id.btnNavigate);
        btnNavigate.setOnClickListener(v -> openGoogleMapsNav());

        Button btnGetLocation = view.findViewById(R.id.btnGetLocation);
        btnGetLocation.setOnClickListener(v -> checkPermissionAndFetch());

        return view;
    }

    private void checkPermissionAndFetch() {
        boolean fine = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (fine || coarse) {
            requestSingleLocationUpdate();
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1001);
        }
    }

    @SuppressLint("MissingPermission")
    private void requestSingleLocationUpdate() {
        tvInfo.setText("⏳ 正在取得目前位置…");
        LocationRequest req = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                2000 // 2s；實際只取一次
        ).setMaxUpdates(1)
                .setMinUpdateIntervalMillis(0)
                .build();

        fused.requestLocationUpdates(req, oneShotCallback, Looper.getMainLooper());
    }

    /** 顯示座標到 tvInfo，並開始計算路徑與時間 */
    private void showLocationAndStartRoute(@NonNull Location loc) {
        tvInfo.setText("📍 目前位置：緯度 " + loc.getLatitude() + ", 經度 " + loc.getLongitude()
                + "\n⏳ 正在計算路徑與時間…");
        fetchRouteToHospital(loc);
    }

    private void fetchRouteToHospital(Location loc) {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://router.project-osrm.org/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OSRMService service = retrofit.create(OSRMService.class);

        // OSRM 參數順序：lon,lat;lon,lat
        service.getRoute(
                loc.getLongitude(), loc.getLatitude(),
                HOSPITAL_LON, HOSPITAL_LAT,
                "false"
        ).enqueue(new Callback<OSRMResponse>() {
            @Override
            public void onResponse(Call<OSRMResponse> call, Response<OSRMResponse> response) {
                if (!response.isSuccessful()) {
                    tvInfo.setText("📍 目前位置：緯度 " + loc.getLatitude() + ", 經度 " + loc.getLongitude()
                            + "\n❌ 路徑計算失敗：HTTP " + response.code());
                    fallbackEstimate(loc);
                    return;
                }
                OSRMResponse body = response.body();
                if (body == null || body.routes == null || body.routes.isEmpty()) {
                    tvInfo.setText("📍 目前位置：緯度 " + loc.getLatitude() + ", 經度 " + loc.getLongitude()
                            + "\n❌ 路徑計算失敗：沒有可用路線");
                    fallbackEstimate(loc);
                    return;
                }
                OSRMResponse.Route r = body.routes.get(0);
                double km = r.distance / 1000.0;
                int minutes = (int) Math.round(r.duration / 60.0);

                tvInfo.setText("📍 目前位置：緯度 " + loc.getLatitude() + ", 經度 " + loc.getLongitude()
                        + "\n🚗 路程約：" + String.format("%.1f 公里", km)
                        + "\n⏱️ 路況良好的情況下，開車需：" + minutes + " 分鐘");
            }

            @Override
            public void onFailure(Call<OSRMResponse> call, Throwable t) {
                tvInfo.setText("📍 目前位置：緯度 " + loc.getLatitude() + ", 經度 " + loc.getLongitude()
                        + "\n❌ 路徑計算錯誤：" + t.getClass().getSimpleName() + " - " + t.getMessage());
                fallbackEstimate(loc);
            }
        });
    }

    /** OSRM 掛了時的備援估算（直線距離 + 市區時速 30km/h） */
    private void fallbackEstimate(Location loc) {
        double km = haversineKm(loc.getLatitude(), loc.getLongitude(), HOSPITAL_LAT, HOSPITAL_LON);
        int minutes = (int) Math.max(1, Math.round(km / 30.0 * 60.0));
        tvInfo.append("\n（備援估算）直線約 " + String.format("%.1f", km) + " 公里，約 " + minutes + " 分鐘");
    }

    /** Haversine 直線距離 (km) */
    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    /** 一鍵開 Google 地圖導航（免費、免金鑰） */
    private void openGoogleMapsNav() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + HOSPITAL_LAT + "," + HOSPITAL_LON + "&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri web = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="
                    + HOSPITAL_LAT + "," + HOSPITAL_LON + "&travelmode=driving");
            startActivity(new Intent(Intent.ACTION_VIEW, web));
        }
    }

    // 權限回應
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestSingleLocationUpdate();
            } else {
                tvInfo.setText("❌ 沒有定位權限");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try { fused.removeLocationUpdates(oneShotCallback); } catch (Exception ignored) {}
    }
}
