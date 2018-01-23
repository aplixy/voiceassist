package com.voiceassist.lixinyu.voiceassist.main.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.AssistApplication;
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity;
import com.voiceassist.lixinyu.voiceassist.common.Constants;
import com.voiceassist.lixinyu.voiceassist.common.widget.LoadingDialog;
import com.voiceassist.lixinyu.voiceassist.entity.AllData;
import com.voiceassist.lixinyu.voiceassist.entity.Node;
import com.voiceassist.lixinyu.voiceassist.entity.Relationship;
import com.voiceassist.lixinyu.voiceassist.main.adapter.GridAdapter;
import com.voiceassist.lixinyu.voiceassist.main.adapter.MainPagerAdapter;
import com.voiceassist.lixinyu.voiceassist.utils.FileUtils;
import com.voiceassist.lixinyu.voiceassist.utils.JsonUtils;
import com.voiceassist.lixinyu.voiceassist.utils.KGLog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final int RC_STORAGE_PERMISSION = 0x100;

    private static final int PAGE_SIZE = 9;

    private String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private TextView mTvMain;
    private ViewPager mViewPagerTop;
    private ViewPager mViewPagerBottom;
    private MainPagerAdapter mPagerAdapterTop;

    private LoadingDialog mLoadingDialog;


    private ArrayMap<String, Node> mNodesMap;
    private ArrayMap<String, Relationship> mRelationMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
    }


    private void initViews() {
        mTvMain = findViewById(R.id.main_text);
        mViewPagerTop = findViewById(R.id.main_viewpager_top);

        mLoadingDialog = new LoadingDialog(this);
    }

    private void initData() {
        getData();
    }



    private void getData() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            getDataAsync();
        } else {
            KGLog.i("请求权限");
            EasyPermissions.requestPermissions(this, "需要您允许存储权限", RC_STORAGE_PERMISSION, perms);
        }
    }


    private void getDataAsync() {
        Observable.just(Constants.JSON_DATA_PATH).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .map(new Function<String, AllData>() {
                    @Override
                    public AllData apply(String filePath) throws Exception {
                        return getDataSync(filePath);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AllData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onNext(AllData value) {
                        renderView(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        mLoadingDialog.dismiss();
                    }
                });
    }

    private AllData getDataSync(String filePath) {
        String str = null;
        if (EasyPermissions.hasPermissions(this, perms)) {
            str = FileUtils.readFileSingleLine(filePath);

            KGLog.d("读取Sd卡结果--->" + str);

            if (null == str) {
                FileUtils.copyFilesFromRawNoSpace(AssistApplication.getInstance(), R.raw.json_data, filePath);
                str = FileUtils.readFileSingleLine(filePath);
                KGLog.i("初次复制数据--->" + str);
            }
        } else {
            KGLog.w("未获取到权限，取备用数据");
            str = FileUtils.readRawNoSpace(AssistApplication.getInstance(), R.raw.json_data);
            KGLog.w("raw数据--->" + str);
        }

        if (null != str) {
            return JsonUtils.getObjectFromJson(str, AllData.class);
        }

        return null;
    }

    private void renderView(AllData allData) {
        if (null == allData || null == allData.nodes || null == allData.relationship) return;

        mNodesMap = new ArrayMap<>();
        for (Node node : allData.nodes) {
            if (null == node) continue;
            mNodesMap.put(node.id, node);
        }

        mRelationMap = new ArrayMap<>();
        ArrayList<View> views = new ArrayList<>();
        List<String> onePageData = null;

        int i = 0;
        for (Relationship relationship : allData.relationship) {
            if (null == relationship) continue;

            mRelationMap.put(relationship.firstLevelNodeId, relationship);

            if (i % PAGE_SIZE == 0) {
                if (i > 0) {
                    views.add(getPageView(onePageData));
                }
                onePageData = new ArrayList<>();
            }

            onePageData.add(mNodesMap.get(relationship.firstLevelNodeId).cnName + " ");

            i++;
        }

        if (allData.relationship.size() % PAGE_SIZE != 0) {
            views.add(getPageView(onePageData));
        }

        mPagerAdapterTop = new MainPagerAdapter(views);
        mViewPagerTop.setAdapter(mPagerAdapterTop);
        
    }


    private View getPageView(final List<String> data) {
        GridView gridView = new GridView(this);

        gridView.setHorizontalSpacing(5);
        gridView.setVerticalSpacing(5);
        gridView.setNumColumns(3);
        gridView.setAdapter(new GridAdapter(this, data, 3));

        return gridView;
    }


    // ========= API 23 permissions granted =========
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch (requestCode) {
            case RC_STORAGE_PERMISSION: {
                getDataAsync();
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        KGLog.d("request code--->" + requestCode);
        switch (requestCode) {
            case RC_STORAGE_PERMISSION: {
                // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
                // This will display a dialog directing them to enable the permission in app settings.
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    new AppSettingsDialog.Builder(this).build().show();
                } else {
                    getDataAsync();
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            getData();
        }
    }
}
