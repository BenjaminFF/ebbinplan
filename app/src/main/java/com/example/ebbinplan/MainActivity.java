package com.example.ebbinplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebbinplan.model.PlanItem;
import com.example.ebbinplan.planitem_list.MyAdapter;
import com.example.ebbinplan.util.DensityUtil;
import com.example.ebbinplan.util.KeyboardUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.w3c.dom.Text;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private CreatePlanDialog createPlanDialog;
    private RecyclerView planItemsRecyclerView;
    private TextView itemEmptyTextView;
    private ImageView titleImage;
    private MyAdapter myAdapter;
    private List<PlanItem> planItems;
    private long minOfTodayTimeStamp = LocalDateTime.now().with(LocalTime.MIN).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LitePal.getDatabase();
        //LitePal.deleteAll(PlanItem.class);
        initTitle();
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void initTitle() {
        titleImage = findViewById(R.id.title_image);
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int[] pics=new int[]{R.drawable.title_bg1,R.drawable.title_bg2, R.drawable.title_bg3,
                        R.drawable.title_bg4,R.drawable.title_bg5,R.drawable.title_bg6,R.drawable.title_bg7};
                int pic=pics[(int)(Math.random()*pics.length)];
                Picasso.get().load(pic).into(titleImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        initCreatePlanDialog();
                        initPlanItemsRecyclerView();
                        initFAB();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });
        /*OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.MILLISECONDS)
                .writeTimeout(50, TimeUnit.MILLISECONDS)
                .readTimeout(50, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder().url("https://api.unsplash.com/users/benjaminff/collections?client_id=1e3f66fcc0a140b1c80fb939b15a012428015720f91793d97b3daf9d6c54ef77").build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("LoadPicInfo", "请求图片列表数据失败");
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get().load(R.drawable.title_bg3).into(titleImage);
                        initCreatePlanDialog();
                        initPlanItemsRecyclerView();
                        initFAB();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String resultJson = response.body().string();//5.获得网络数据
                    JSONArray jsonArray = new JSONArray(resultJson);
                    //final String picture = jsonArray.getJSONObject(0).getJSONObject("preview_photos").getJSONObject("").getString("raw") + "&h=800&fit=max";
                    JSONArray photos = jsonArray.getJSONObject(0).getJSONArray("preview_photos");
                    int picHeight= DensityUtil.dip2px(MainActivity.this,260);
                    final String picture_url=photos.getJSONObject((int)(photos.length()*Math.random())).getJSONObject("urls").getString("raw")+ "&h="+picHeight+"&fit=max";
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get().load(picture_url).networkPolicy(NetworkPolicy.OFFLINE).into(titleImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("LoadPicInfo", "加载缓存图片成功");
                                    initCreatePlanDialog();
                                    initPlanItemsRecyclerView();
                                    initFAB();
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.d("LoadPicInfo", "加载缓存图片失败");
                                    Picasso.get().load(picture_url).into(titleImage, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("LoadPicInfo", "加载网络图片成功");
                                            initCreatePlanDialog();
                                            initPlanItemsRecyclerView();
                                            initFAB();
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Log.d("LoadPicInfo", "加载网络图片失败");
                                            //加载本地图片
                                        }
                                    });
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                }
            }
        });*/
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(planItems.size()==0){
                            itemEmptyTextView.setVisibility(View.VISIBLE);
                        }
                    }
                },200);
            }
        });
        planItemsRecyclerView.setAdapter(myAdapter);
        itemEmptyTextView=findViewById(R.id.main_item_empty_text);
        if(planItems.size()==0){
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
        for (int i = 0; i < dayInternals.length; i++) {
            PlanItem planItem = new PlanItem();
            planItem.setName(planName);
            planItem.setPlanId(randomUUID);
            planItem.setTimestamp(minOfTodayTimeStamp + dayInternals[i] * 24 * 60 * 60 * 1000L);
            planItem.save();
            if (i == 0) {
                planItems.add(planItem);
            }
        }
        if(itemEmptyTextView.getVisibility()==View.VISIBLE){
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    itemEmptyTextView.setVisibility(View.INVISIBLE);
                }
            });
        }
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
        createPlanDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                myAdapter.notifyItemInserted(planItems.size()-1);
            }
        });
        /*createPlanDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        KeyboardUtil.toggleKeyboard(MainActivity.this);
                    }
                },200);
            }
        });*/
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
