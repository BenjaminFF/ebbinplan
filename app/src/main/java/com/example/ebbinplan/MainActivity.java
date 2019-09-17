package com.example.ebbinplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ebbinplan.tables.Plan;

import org.litepal.LitePal;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        List<Plan> plans = LitePal.where("name like ?","test").find(Plan.class);
        /*int[] timeStamps=new int[] {111,222};
        plans.get(0).setTimeStamps(timeStamps);
        plans.get(0).save();*/
        Toast.makeText(this,plans.get(0).toString(),Toast.LENGTH_LONG).show();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.mtoolbar);
        toolbar.setTitle("anbinplan");
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
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.toolbar_filter:
                showPopupMenu(findViewById(R.id.toolbar_filter));
                break;
        }
        return super.onOptionsItemSelected(item);
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
}
