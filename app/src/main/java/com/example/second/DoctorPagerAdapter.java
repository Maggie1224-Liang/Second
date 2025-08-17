package com.example.second;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DoctorPagerAdapter extends RecyclerView.Adapter<DoctorPagerAdapter.DoctorViewHolder> {

    private List<Doctor> doctorList;

    public DoctorPagerAdapter(List<Doctor> doctorList) {
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.textDoctorName.setText("醫師：" + doctor.getName());
        holder.textDeputyDoctor.setText("代理醫師：" + doctor.getDeputy());
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView textDoctorName, textDeputyDoctor;

        DoctorViewHolder(View itemView) {
            super(itemView);
            textDoctorName = itemView.findViewById(R.id.textDoctorName);
            textDeputyDoctor = itemView.findViewById(R.id.textDeputyDoctor);
        }
    }
}
