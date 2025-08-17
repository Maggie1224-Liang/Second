package com.example.second;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment {

    private Button btnMorning, btnAfternoon, btnNight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnMorning   = view.findViewById(R.id.btnMorning);
        btnAfternoon = view.findViewById(R.id.btnAfternoon);
        btnNight     = view.findViewById(R.id.btnNight);

        // 確保不受主題 tint 影響（避免紫色）
        btnMorning.setBackgroundTintList(null);
        btnAfternoon.setBackgroundTintList(null);
        btnNight.setBackgroundTintList(null);

        // 預設：選取「上午診」（如不需要，註解掉下一行即可）
        selectSession(btnMorning);

        View.OnClickListener click = v -> {
            if (v == btnMorning)       selectSession(btnMorning);
            else if (v == btnAfternoon) selectSession(btnAfternoon);
            else if (v == btnNight)     selectSession(btnNight);
        };

        btnMorning.setOnClickListener(click);
        btnAfternoon.setOnClickListener(click);
        btnNight.setOnClickListener(click);
    }

    /** 互斥選取：選到 target，其餘恢復未選 */
    private void selectSession(@NonNull Button target) {
        setUnselected(btnMorning);
        setUnselected(btnAfternoon);
        setUnselected(btnNight);
        setSelected(target);
    }

    private void setSelected(@NonNull Button b) {
        b.setBackgroundResource(R.drawable.pill_selected);
        b.setTextColor(0xFFFFFFFF); // 白字
    }

    private void setUnselected(@NonNull Button b) {
        b.setBackgroundResource(R.drawable.pill_unselected);
        b.setTextColor(0xFF3F51B5); // 藍字（可改成你要的顏色）
    }
}
