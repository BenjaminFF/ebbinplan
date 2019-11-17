package com.example.ebbinplan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.example.ebbinplan.model.PlanItem;
import com.example.ebbinplan.planitem_list.MyAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.litepal.LitePal;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private CreatePlanDialog createPlanDialog;
    private RecyclerView planItemsRecyclerView;
    private TextView itemEmptyTextView;
    private TextView planTextView;
    private ImageView titleImage;
    private MyAdapter myAdapter;
    private List<PlanItem> planItems;
    private RippleView mPlanRippleView;
    private long minOfTodayTimeStamp = LocalDateTime.now().with(LocalTime.MIN).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    private boolean isTodayPlan = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(android.R.id.content);
        Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        mLoadAnimation.setDuration(1000);
        view.startAnimation(mLoadAnimation);

        LitePal.getDatabase();
        //LitePal.deleteAll(PlanItem.class);
        initTitle();
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void initTitle() {
        titleImage = findViewById(R.id.title_image);
        titleImage.setAdjustViewBounds(true);
        int[] pics = new int[]{R.mipmap.header_bg1, R.mipmap.header_bg2,
                R.mipmap.header_bg1, R.mipmap.header_bg4,
                R.mipmap.header_bg5, R.mipmap.header_bg6, R.mipmap.header_bg7, R.mipmap.header_bg8};
        int pic = pics[(int) (Math.random() * pics.length)];
        Picasso.get().load(pic).into(titleImage, new Callback() {
            @Override
            public void onSuccess() {
                View mainLayout = findViewById(R.id.main_layout);
                mainLayout.setVisibility(View.VISIBLE);
                initCreatePlanDialog();
                initRipple();
                initPlanItemsRecyclerView();
                initFAB();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void initRipple() {
        mPlanRippleView = findViewById(R.id.mPlanRippleView);
        planTextView = findViewById(R.id.planTextView);
        mPlanRippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                isTodayPlan = !isTodayPlan;
                String mText = isTodayPlan ? "今日计划" : "全部计划";
                planTextView.setText(mText);
                togglePlan();
            }
        });
    }

    private void initFAB() {
        fab = findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlanDialog.show();
            }
        });
    }

    private void initPlanItemsRecyclerView() {
        planItems = LitePal.where("timestamp like ?", minOfTodayTimeStamp + "").find(PlanItem.class);
        planItemsRecyclerView = findViewById(R.id.planitems_recyclerview);
        planItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(planItems);
        myAdapter.setOnDeleteItemListener(new MyAdapter.OnDeleteItemListener() {
            @Override
            public void onDeleteItem(int position) {
                if (isTodayPlan) {
                    LitePal.delete(PlanItem.class, planItems.get(position).getId());
                } else {
                    LitePal.deleteAll(PlanItem.class, "planId=?", planItems.get(position).getPlanId() + "");
                }
                planItems.remove(position);
                myAdapter.notifyItemRemoved(position);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (planItems.size() == 0) {
                            itemEmptyTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }, 200);
            }
        });
        planItemsRecyclerView.setAdapter(myAdapter);
        itemEmptyTextView = findViewById(R.id.main_item_empty_text);
        if (planItems.size() == 0) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    itemEmptyTextView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void createPlan(String planName) {
        String randomUUID = UUID.randomUUID().toString();
        int[] dayInternals = new int[]{0, 1, 2, 4, 7, 14, 21, 28};
        for (int i = 0; i < (isTodayPlan ? 1 : dayInternals.length); i++) {
            PlanItem planItem = new PlanItem();
            planItem.setName(planName);
            planItem.setPlanId(randomUUID);
            planItem.setTimestamp(minOfTodayTimeStamp + dayInternals[i] * 24 * 60 * 60 * 1000L);
            planItem.save();
            if (i == 0) {
                planItems.add(planItem);
            }
        }
        if (itemEmptyTextView.getVisibility() == View.VISIBLE) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    itemEmptyTextView.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void togglePlan() {
        planItems.clear();
        if (isTodayPlan) {
            planItems.addAll(LitePal.where("timestamp like ?", minOfTodayTimeStamp + "").find(PlanItem.class));
        } else {
            List<PlanItem> planList = LitePal.findAll(PlanItem.class);
            List<PlanItem> newPlanList = new ArrayList<>(planList.size());
            for (int i = 0; i < planList.size(); i++) {
                boolean hasItem = false;
                for (PlanItem item : newPlanList) {
                    if (item.getPlanId().equals(planList.get(i).getPlanId())) {
                        hasItem = true;
                        break;
                    }
                }
                if (!hasItem) {
                    newPlanList.add(planList.get(i));
                }
            }
            planItems.addAll(newPlanList);
        }

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int isVisible = planItems.size() == 0 ? View.VISIBLE : View.INVISIBLE;
                itemEmptyTextView.setVisibility(isVisible);
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initCreatePlanDialog() {
        createPlanDialog = new CreatePlanDialog(MainActivity.this);
        createPlanDialog.setOnConfirmClickListener(new CreatePlanDialog.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String planName, EditText editText) {
                if (planName.equals("")) {
                    String string = getString(R.string.empty_error);
                    editText.setError(string);
                    return;
                }

                if (LitePal.where("name like ?", planName).find(PlanItem.class).size() != 0) {
                    String string = getString(R.string.duplicate_error);
                    editText.setError(string);
                    return;
                }

                createPlan(planName);
                createPlanDialog.dismiss();
                editText.setText("");
                myAdapter.notifyItemInserted(planItems.size() - 1);
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
                }, 100);
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
