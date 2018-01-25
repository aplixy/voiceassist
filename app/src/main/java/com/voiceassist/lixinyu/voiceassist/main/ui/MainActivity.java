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

import com.tencent.bugly.beta.Beta;
import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.AssistApplication;
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity;
import com.voiceassist.lixinyu.voiceassist.common.Constants;
import com.voiceassist.lixinyu.voiceassist.common.widget.LoadingDialog;
import com.voiceassist.lixinyu.voiceassist.common.widget.ViewPagerPointer;
import com.voiceassist.lixinyu.voiceassist.entity.dto.INodeId;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Relationship;
import com.voiceassist.lixinyu.voiceassist.entity.vo.AllData;
import com.voiceassist.lixinyu.voiceassist.entity.vo.GridViewVo;
import com.voiceassist.lixinyu.voiceassist.main.adapter.GridAdapter;
import com.voiceassist.lixinyu.voiceassist.main.adapter.GridAdapterFirstLevel;
import com.voiceassist.lixinyu.voiceassist.main.adapter.GridAdapterSecondLevel;
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

    private static final String TAG = "MainActivity";

    private static final int RC_STORAGE_PERMISSION = 0x100;

    private static final int PAGE_SIZE = 9;

    private String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private ViewPager mViewPagerFirstLevel;
    private ViewPager mViewPagerSecondLevel;
    private TextView mTvFirstLevelName;

    private MainPagerAdapter mPagerAdapterFirstLevel;
    private MainPagerAdapter mPagerAdapterSecondLevel;

    private LoadingDialog mLoadingDialog;


    private ArrayMap<String, Node> mNodesMap;

    private GridAdapter.OnItemClickListener mOnFirstLevelClickListener;

    private ViewPagerPointer mPagerPointerFirstLevel;
    private ViewPagerPointer mPagerPointerSecondLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
        initListener();

        Beta.checkUpgrade(false, true);
    }


    private void initViews() {
        mViewPagerFirstLevel = findViewById(R.id.main_viewpager_first_level);
        mViewPagerSecondLevel = findViewById(R.id.main_viewpager_second_level);
        mTvFirstLevelName = findViewById(R.id.main_textview_first_level_name);

        mLoadingDialog = new LoadingDialog(this);

        mPagerPointerFirstLevel = findViewById(R.id.main_pager_indicator_first_level);
        mPagerPointerSecondLevel = findViewById(R.id.main_pager_indicator_second_level);
    }

    private void initData() {
        getData();
    }

    private void initListener() {
        mOnFirstLevelClickListener = new GridAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, GridViewVo vo) {
                if (null == vo || null == vo.relationship || null == vo.relationship.secondLevelNodes) {
                    mTvFirstLevelName.setText("");
                    return;
                } else if (null != vo.node){
                    mTvFirstLevelName.setText(vo.node.cnName);
                }

                if (null != vo.relationship && null != vo.relationship.secondLevelNodes && vo.relationship.secondLevelNodes.size() > 0) {
                    mPagerPointerSecondLevel.setVisibility(View.VISIBLE);
                } else {
                    mPagerPointerSecondLevel.setVisibility(View.INVISIBLE);
                }

                mPagerAdapterSecondLevel = new MainPagerAdapter(getPagers(vo.relationship.secondLevelNodes));
                mViewPagerSecondLevel.setAdapter(mPagerAdapterSecondLevel);
                mPagerPointerSecondLevel.setViewPager(mViewPagerSecondLevel);
            }
        };

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
                        AllData allData = getDataSync(filePath);

                        if (null != allData && null != allData.nodes) {
                            mNodesMap = new ArrayMap<>();
                            for (Node node : allData.nodes) {
                                if (null == node) continue;
                                mNodesMap.put(node.id, node);
                            }
                        }

                        return allData;
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
        if (null == allData) return;

        mPagerAdapterFirstLevel = new MainPagerAdapter(getPagers(allData.relationship));
        mViewPagerFirstLevel.setAdapter(mPagerAdapterFirstLevel);

        mPagerPointerFirstLevel.setViewPager(mViewPagerFirstLevel);

    }


    private View getOnePageFirstLevel(List<GridViewVo> data) {
        GridView gridView = new GridView(this);

        gridView.setHorizontalSpacing(9);
        gridView.setVerticalSpacing(9);
        gridView.setNumColumns(3);
        GridAdapter adapter = new GridAdapterFirstLevel(this, data, 3);
        adapter.setOnItemClickListener(mOnFirstLevelClickListener);
        gridView.setAdapter(adapter);

        return gridView;
    }

    private View getOnePageSecondLevel(List<GridViewVo> data) {
        GridView gridView = new GridView(this);

        gridView.setHorizontalSpacing(9);
        gridView.setVerticalSpacing(9);
        gridView.setNumColumns(3);
        gridView.setAdapter(new GridAdapterSecondLevel(this, data, 3));

        return gridView;
    }

    private <T extends INodeId> ArrayList<View> getPagers(List<T> noteIdList) {
        if (null == noteIdList) return null;


        ArrayList<View> views = new ArrayList<>();
        List<GridViewVo> onePageData = new ArrayList<>();

        int nodeType = -1;
        int onePageSize = -1;

        for (INodeId nodeIdEntity : noteIdList) {
            if (null == nodeIdEntity) continue;

            if (-1 == nodeType) nodeType = nodeIdEntity.getNodeType();

            onePageSize = onePageData.size();

            // 在最一开始先生成ViewPager的一个新View，后续再给onePageData填数据进去
            if (onePageSize == 0 || onePageSize >= PAGE_SIZE) {
                if (onePageSize > 0) onePageData = new ArrayList<>();

                View view = null;
                if (INodeId.NODE_TYPE_FIRST_LEVEL == nodeType) {
                    view = getOnePageFirstLevel(onePageData);
                } else if (INodeId.NODE_TYPE_SECOND_LEVEL == nodeType) {
                    view = getOnePageSecondLevel(onePageData);
                }
                if (null != view) views.add(view);
            }

            // 给onePageData加一个节点
            Node node = mNodesMap.get(nodeIdEntity.getNodeId());
            if (null == node) continue;// 节点为空则不加数据

            Relationship relationship = null;
            if (nodeIdEntity instanceof Relationship) {
                relationship = (Relationship) nodeIdEntity;
            }
            onePageData.add(new GridViewVo(node, relationship));

        }

        return views;
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
