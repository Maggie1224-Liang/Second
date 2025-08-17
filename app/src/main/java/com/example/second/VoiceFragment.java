package com.example.second;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class VoiceFragment extends Fragment {

    private LinearLayout chatContainer;
    private ScrollView chatScroll;
    private EditText editMessage;
    private ImageButton btnSend;

    public VoiceFragment() {
        // 必須要有空的建構子
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai, container, false);

        chatContainer = view.findViewById(R.id.chatContainer);
        chatScroll = view.findViewById(R.id.chatScroll);
        editMessage = view.findViewById(R.id.editMessage);
        btnSend = view.findViewById(R.id.btnSend);

        // 每次進入頁面時顯示 AI 歡迎訊息
        addMessageBubble("您好，我是值得您信任的小幫手", false);

        // 送出按鈕
        btnSend.setOnClickListener(v -> {
            String message = editMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                // 顯示使用者訊息
                addMessageBubble(message, true);
                editMessage.setText("");

                // 確保滾動到底
                scrollToBottom();

                // 模擬 AI 回覆
                editMessage.postDelayed(() -> {
                    addMessageBubble("這是 AI 回覆的內容", false);
                    scrollToBottom();
                }, 500);
            }
        });

        return view;
    }

    /**
     * 新增訊息泡泡（含頭像）
     * @param text   訊息內容
     * @param isUser 是否是使用者
     */
    private void addMessageBubble(String text, boolean isUser) {
        // 外層水平 LinearLayout
        LinearLayout messageLayout = new LinearLayout(getContext());
        messageLayout.setOrientation(LinearLayout.HORIZONTAL);
        messageLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        messageLayout.setPadding(8, 8, 8, 8);

        // 頭像
        ImageView avatar = new ImageView(getContext());
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(80, 80);
        avatar.setLayoutParams(avatarParams);
        avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        avatar.setImageResource(isUser ? R.drawable.ic_user_avatar : R.drawable.ic_ai_avatar);

        // 訊息泡泡
        TextView bubble = new TextView(getContext());
        bubble.setText(text);
        bubble.setTextSize(16);
        bubble.setPadding(20, 12, 20, 12);
        bubble.setBackgroundResource(isUser ? R.drawable.user_bubble : R.drawable.bot_bubble);
        bubble.setTextColor(isUser ? Color.WHITE : Color.BLACK);

        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        bubbleParams.setMargins(12, 0, 12, 0);
        bubble.setLayoutParams(bubbleParams);

        // 排列方式：使用者在右邊，AI 在左邊
        if (isUser) {
            messageLayout.setGravity(Gravity.END);
            messageLayout.addView(bubble);
            messageLayout.addView(avatar);
        } else {
            messageLayout.setGravity(Gravity.START);
            messageLayout.addView(avatar);
            messageLayout.addView(bubble);
        }

        chatContainer.addView(messageLayout);
    }

    /**
     * 捲動到聊天最底部（延遲確保 UI 已更新）
     */
    private void scrollToBottom() {
        chatScroll.postDelayed(() -> chatScroll.fullScroll(View.FOCUS_DOWN), 100);
    }
}
