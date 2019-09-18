package com.example.ebbinplan;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CreatePlanDialog extends Dialog {

    public CreatePlanDialog(@NonNull Context context) {
        super(context);
    }

    public CreatePlanDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        setContentView(R.layout.create_plan_dialog);
        initDialogSizeAndPosition();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_plan_dialog);
        initDialogSizeAndPosition();
    }

    private void initDialogSizeAndPosition(){
        WindowManager wm = getWindow().getWindowManager();
        Display display = wm.getDefaultDisplay(); //获取屏幕宽高
        Point point = new Point();
        display.getSize(point);

        Window dialogWindow=getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes(); //获取当前对话框的参数值
        layoutParams.width = (int) (point.x * 0.9); //宽度设置为屏幕宽度的0.5
        layoutParams.height = (int) (point.y * 0.5); //高度设置为屏幕高度的0.5
        dialogWindow.setAttributes(layoutParams);
        dialogWindow.setGravity(Gravity.CENTER);
    }
}
