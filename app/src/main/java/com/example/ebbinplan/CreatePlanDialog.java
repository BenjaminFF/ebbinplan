package com.example.ebbinplan;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ebbinplan.util.KeyboardUtil;

import java.util.List;

public class CreatePlanDialog extends Dialog {

    private Button confirmButton;
    private Button cancelButton;
    private EditText editNameText;
    private Context mContext;

    private OnConfirmClickListener onConfirmClickListener;//取消按钮被点击了的监听器
    private OnCancelClickListener onCancelClickListener;//确定按钮被点击了的监听器

    public CreatePlanDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        initDialogSizeAndPosition();
        setCanceledOnTouchOutside(false);
    }

    public CreatePlanDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        setContentView(R.layout.create_plan_dialog);
        initDialogSizeAndPosition();
        setCanceledOnTouchOutside(false);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (isOutOfBounds(getContext(), event)) {
            editNameText.setError(null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            },100);
        }
        return super.onTouchEvent(event);
    }


    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface OnConfirmClickListener {
        void onConfirmClick(String planName, EditText editText);
    }

    public interface OnCancelClickListener {
        void onCancelClick(EditText editText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_plan_dialog);
        initDialogSizeAndPosition();
        bindListener();
    }

    private void bindListener(){
        editNameText=findViewById(R.id.cpd_edit_name_text);
        confirmButton=findViewById(R.id.cpd_confirm_button);
        cancelButton=findViewById(R.id.cpd_cancel_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onConfirmClickListener!=null){
                    onConfirmClickListener.onConfirmClick(editNameText.getText().toString(), editNameText);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCancelClickListener!=null){
                    onCancelClickListener.onCancelClick(editNameText);
                }
            }
        });

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                editNameText.setError(null);
            }
        });
    }

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener){
        this.onConfirmClickListener=onConfirmClickListener;
    }

    public void setOnCancelClickListener(OnCancelClickListener onCancelClickListener){
        this.onCancelClickListener=onCancelClickListener;
    }

    private void initDialogSizeAndPosition(){
        WindowManager wm = getWindow().getWindowManager();
        Display display = wm.getDefaultDisplay(); //获取屏幕宽高
        Point point = new Point();
        display.getSize(point);

        Window dialogWindow=getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes(); //获取当前对话框的参数值
        layoutParams.width = (int) (point.x * 0.9); //宽度设置为屏幕宽度的0.5
        //layoutParams.height = (int) (point.y * 0.5); //高度设置为屏幕高度的0.5
        dialogWindow.setAttributes(layoutParams);
        dialogWindow.setGravity(Gravity.CENTER);
    }
}
