package com.example.second;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        // 診別 Spinner
        Spinner spinnerDepartment = view.findViewById(R.id.spinner_department);
        String[] departments = {"神經外科", "家醫科", "骨科"};
        ArrayAdapter<String> depAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, departments);
        spinnerDepartment.setAdapter(depAdapter);

        // 午別 Spinner
        Spinner spinnerSession = view.findViewById(R.id.spinner_session);
        String[] sessions = {"上午診", "下午診"};
        ArrayAdapter<String> sesAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, sessions);
        spinnerSession.setAdapter(sesAdapter);

        // 醫師資料（假資料示範）
        List<Doctor> doctorList = new ArrayList<>();
        doctorList.add(new Doctor("XXX", "---"));
        doctorList.add(new Doctor("YYY", "AAA"));
        doctorList.add(new Doctor("ZZZ", "BBB"));

        // 設定 ViewPager2
        ViewPager2 viewPagerDoctor = view.findViewById(R.id.viewPagerDoctor);
        viewPagerDoctor.setAdapter(new DoctorPagerAdapter(doctorList));

        return view;
    }
}
