package com.example.ebbinplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ebbinplan.model.PlanItem;
import com.example.ebbinplan.util.KeyboardUtil;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    CreatePlanDialog createPlanDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initCreatePlanDialog();
        LitePal.getDatabase();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_add:
                createPlanDialog.show();
                break;
            case R.id.toolbar_filter:
                showPopupMenu(findViewById(R.id.toolbar_filter));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createPlan(String planName) {
        String randomUUID = UUID.randomUUID().toString();
        Long minOfTodayTimeStamp = LocalDateTime.now().with(LocalTime.MIN).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        int[] dayInternals = new int[]{1, 2, 4, 7, 14, 21, 28};
        for (int i = 0; i < dayInternals.length; i++) {
            PlanItem planItem = new PlanItem();
            planItem.setName(planName);
            planItem.setPlanId(randomUUID);
            planItem.setTimestamp(minOfTodayTimeStamp + dayInternals[i] * 24 * 60 * 60 *1000L);
            planItem.save();
        }
    }

    private void showPopupMenu(View view) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view, Gravity.CENTER, R.attr.actionOverflowMenuStyle, 0);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.filter_menu, popupMenu.getMenu());
        popupMenu.show();
        // 通过上面这几行代码，就可以把控件显示出来了
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 控件每一个item的点击事件
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // 控件消失时的事件
            }
        });
    }

    private void initCreatePlanDialog() {
        final Activity mActivity=this;
        createPlanDialog = new CreatePlanDialog(MainActivity.this);
        createPlanDialog.setOnConfirmClickListener(new CreatePlanDialog.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String planName, EditText editText) {
                if(planName.equals("")){
                    String string = getString(R.string.empty_error);
                    editText.setError(string);
                    return;
                }

                if(LitePal.where("name like ?",planName).find(PlanItem.class).size()!=0){
                    String string = getString(R.string.duplicate_error);
                    editText.setError(string);
                    return;
                }

                createPlan(planName);
                createPlanDialog.dismiss();
            }
        });
        createPlanDialog.setOnCancelClickListener(new CreatePlanDialog.OnCancelClickListener() {
            @Override
            public void onCancelClick(EditText editText) {
                editText.setError(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createPlanDialog.dismiss();
                    }
                },100);
            }
        });
    }

    private void switchLanguage(String language) {
        //设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("zh_simple")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);
        //更新语言后，destroy当前页面，重新绘制
        finish();
        Intent it = new Intent(MainActivity.this, MainActivity.class);
        //清空任务栈确保当前打开activit为前台任务栈栈顶
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
    }
}
