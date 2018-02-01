package com.voiceassist.lixinyu.voiceassist.main.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
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
import com.voiceassist.lixinyu.voiceassist.entity.dto.AllData;
import com.voiceassist.lixinyu.voiceassist.entity.dto.INodeId;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Relationship;
import com.voiceassist.lixinyu.voiceassist.entity.dto.SecondLevelNode;
import com.voiceassist.lixinyu.voiceassist.entity.vo.GridViewVo;
import com.voiceassist.lixinyu.voiceassist.main.adapter.GridAdapter;
import com.voiceassist.lixinyu.voiceassist.main.adapter.GridAdapterFirstLevel;
import com.voiceassist.lixinyu.voiceassist.main.adapter.GridAdapterSecondLevel;
import com.voiceassist.lixinyu.voiceassist.main.adapter.MainPagerAdapter;
import com.voiceassist.lixinyu.voiceassist.settings.ui.EditActivity;
import com.voiceassist.lixinyu.voiceassist.utils.FileUtils;
import com.voiceassist.lixinyu.voiceassist.utils.JsonUtils;
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";

    private static final int PAGE_SIZE = 9;

    private static final int CAN_EXIT_FLAG = 0x100;
    private static final int CAN_ENTER_FLAG = 2;
    private static final int IDLE_FLAG = -1;

    private static final int RC_STORAGE_PERMISSION = 0x100;
    private static final String[] PERMS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.RECORD_AUDIO};

    private static final int REQ_EDIT_DATA = 0x100;

    public static AllData mAllData;
    public static ArrayMap<String, Node> mNodesMap;

    private ViewPager mLevel1ViewPager;
    private ViewPager mLevel2ViewPager;
    private TextView mLevel1NameTv;

    private LoadingDialog mLoadingDialog;

    private GridAdapter.OnItemClickListener mOnLevel1ItemClickListener;

    private ViewPagerPointer mLevel1PagerPointer;
    private ViewPagerPointer mLevel2PagerPointer;

    private Consumer<Long> mExitConsumer;
    private Consumer<Long> mTempSettingConsumer;
    private Disposable mSettingDisposable;

    private int mExitFlag = IDLE_FLAG;
    private int mTempSettingFlag = IDLE_FLAG;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        initViews();
        initData();
        initListener();

        Beta.checkUpgrade(false, true);

    }


    private void initViews() {
        mLevel1ViewPager = findViewById(R.id.main_viewpager_first_level);
        mLevel2ViewPager = findViewById(R.id.main_viewpager_second_level);
        mLevel1NameTv = findViewById(R.id.main_textview_first_level_name);

        mLoadingDialog = new LoadingDialog(this);

        mLevel1PagerPointer = findViewById(R.id.main_pager_indicator_first_level);
        mLevel2PagerPointer = findViewById(R.id.main_pager_indicator_second_level);
    }

    private void initData() {
        getData();

        mExitConsumer = new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                mExitFlag = IDLE_FLAG;
            }
        };

        mTempSettingConsumer = new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                mSettingDisposable = null;
                mTempSettingFlag = IDLE_FLAG;
            }
        };

    }

    private void initListener() {
        mOnLevel1ItemClickListener = new GridAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, GridViewVo vo) {

                if (null != vo.node) {
                    mLevel1NameTv.setText(vo.node.cnName);
                }

                List<SecondLevelNode> secondLevelNodes = null;
                if (null != vo && null != vo.relationship && null != vo.relationship.secondLevelNodes && vo.relationship.secondLevelNodes.size() > 0) {
                    mLevel2PagerPointer.setVisibility(View.VISIBLE);
                    secondLevelNodes = vo.relationship.secondLevelNodes;
                } else {
                    mLevel2PagerPointer.setVisibility(View.INVISIBLE);
                }

                mLevel2ViewPager.setAdapter(new MainPagerAdapter(getPagers(secondLevelNodes)));
                mLevel2PagerPointer.setViewPager(mLevel2ViewPager);
            }
        };

        mLevel1NameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTempSettingFlag < CAN_ENTER_FLAG) {
                    mTempSettingFlag++;
                    if (mTempSettingFlag == CAN_ENTER_FLAG) {
                        // Enter
                        startActivityForResult(new Intent(MainActivity.this, EditActivity.class), REQ_EDIT_DATA);
                    } else {
                        if (null != mSettingDisposable) mSettingDisposable.dispose();
                        mSettingDisposable = Observable.timer(1, TimeUnit.SECONDS).subscribe(mTempSettingConsumer);
                    }
                }
            }
        });


    }


    private void getData() {
        if (EasyPermissions.hasPermissions(this, PERMS)) {
            getDataAsync();
        } else {
            EasyPermissions.requestPermissions(this, "需要您允许存储权限", RC_STORAGE_PERMISSION, PERMS);
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
        String sdCardStr = null;
        String rawStr = FileUtils.readRawNoSpace(AssistApplication.getInstance(), R.raw.json_data);

        AllData rawAllData = null;
        AllData sdCardAllData = null;
        AllData allData = null;

        if (EasyPermissions.hasPermissions(this, PERMS)) {
            sdCardStr = FileUtils.readFileSingleLine(filePath);

            if (null == sdCardStr) {
                FileUtils.copyFilesFromRawNoSpace(AssistApplication.getInstance(), R.raw.json_data, filePath);
                sdCardStr = rawStr;

                if (null != rawStr) return JsonUtils.getObjectFromJson(rawStr, AllData.class);
            }
        }

        if (null != rawStr) {
            allData = rawAllData = JsonUtils.getObjectFromJson(rawStr, AllData.class);
        }

        if (null != sdCardStr) {
            allData = sdCardAllData = JsonUtils.getObjectFromJson(sdCardStr, AllData.class);
        }

        if (null != rawAllData && null != sdCardAllData) {
            if (rawAllData.version > sdCardAllData.version) {
                allData = rawAllData;
                FileUtils.copyFilesFromRawNoSpace(AssistApplication.getInstance(), R.raw.json_data, filePath);
            } else {
                allData = sdCardAllData;
            }
        }

        return allData;
    }

    private void renderView(AllData allData) {
        mAllData = allData;

        List<Relationship> relationshipList = null;
        if (null != allData) relationshipList = allData.relationship;

        // 渲染一级菜单
        mLevel1ViewPager.setAdapter(new MainPagerAdapter(getPagers(relationshipList)));
        mLevel1PagerPointer.setViewPager(mLevel1ViewPager);

        // 渲染二级菜单
        mLevel2ViewPager.setAdapter(new MainPagerAdapter(null));
        mLevel2PagerPointer.setViewPager(mLevel2ViewPager);

        // 初始化指示器
        mLevel1NameTv.setText("点击按钮发音");
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
            if (views.size() == 0 || onePageSize >= PAGE_SIZE) {
                if (onePageSize > 0) onePageData = new ArrayList<>();

                View view = null;
                if (INodeId.NODE_TYPE_FIRST_LEVEL == nodeType) {
                    view = getLevel1OnePage(onePageData);
                } else if (INodeId.NODE_TYPE_SECOND_LEVEL == nodeType) {
                    view = getLevel2OnePage(onePageData);
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
        }// for循环结束

        int pageSize = views.size();
        if (0 == onePageData.size() && pageSize > 0) {
            views.remove(pageSize - 1);
        }

        return views;
    }


    private View getLevel1OnePage(List<GridViewVo> data) {
        GridView gridView = new GridView(this);

        gridView.setHorizontalSpacing(9);
        gridView.setVerticalSpacing(9);
        gridView.setNumColumns(3);
        GridAdapter adapter = new GridAdapterFirstLevel(this, data, 3);
        adapter.setOnItemClickListener(mOnLevel1ItemClickListener);
        gridView.setAdapter(adapter);

        return gridView;
    }

    private View getLevel2OnePage(List<GridViewVo> data) {
        GridView gridView = new GridView(this);

        gridView.setHorizontalSpacing(9);
        gridView.setVerticalSpacing(9);
        gridView.setNumColumns(3);
        gridView.setAdapter(new GridAdapterSecondLevel(this, data, 3));

        return gridView;
    }


    public static void saveAllDatas() {
        String jsonData = JsonUtils.getJsonFromObject(MainActivity.mAllData);
        FileUtils.saveTextToFile(jsonData, Constants.JSON_DATA_PATH);

        ToastUtils.showToast("编辑生效");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (mExitFlag == CAN_EXIT_FLAG) {
                finish();
                //System.exit(0);
            } else {
                mExitFlag = CAN_EXIT_FLAG;
                Observable.timer(2, TimeUnit.SECONDS).subscribe(mExitConsumer);
                ToastUtils.showToast("再按一次退出应用");
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        switch (requestCode) {
            case RC_STORAGE_PERMISSION: {
                // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
                // This will display a dialog directing them to enable the permission in app settings.
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    new AppSettingsDialog.Builder(this).build().show();// 用户选择『不开提示』时引导用户手动开启权限
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

        switch (requestCode) {
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE: {// 用户手动开启权限后调用
                // Do something after user returned from app settings screen, like showing a Toast.
                getData();
                break;
            }

            case REQ_EDIT_DATA: {
                renderView(mAllData);
                break;
            }

            default: {
                break;
            }
        }
    }
}
