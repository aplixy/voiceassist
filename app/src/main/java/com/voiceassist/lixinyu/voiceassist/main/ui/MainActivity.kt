package com.voiceassist.lixinyu.voiceassist.main.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewStub
import android.widget.GridView
import android.widget.TextView

import com.tencent.bugly.beta.Beta
import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.common.AssistApplication
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity
import com.voiceassist.lixinyu.voiceassist.common.Constants
import com.voiceassist.lixinyu.voiceassist.common.rx.RxHelper
import com.voiceassist.lixinyu.voiceassist.common.widget.LoadingDialog
import com.voiceassist.lixinyu.voiceassist.common.widget.ViewPagerPointer
import com.voiceassist.lixinyu.voiceassist.entity.dto.AllData
import com.voiceassist.lixinyu.voiceassist.entity.dto.INodeId
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node
import com.voiceassist.lixinyu.voiceassist.entity.dto.Relationship
import com.voiceassist.lixinyu.voiceassist.entity.dto.SecondLevelNode
import com.voiceassist.lixinyu.voiceassist.entity.vo.GridViewVo
import com.voiceassist.lixinyu.voiceassist.main.adapter.GridAdapter
import com.voiceassist.lixinyu.voiceassist.main.adapter.GridAdapterFirstLevel
import com.voiceassist.lixinyu.voiceassist.main.adapter.GridAdapterSecondLevel
import com.voiceassist.lixinyu.voiceassist.main.adapter.MainPagerAdapter
import com.voiceassist.lixinyu.voiceassist.settings.ui.EditActivity
import com.voiceassist.lixinyu.voiceassist.settings.ui.IEmptyable
import com.voiceassist.lixinyu.voiceassist.settings.ui.SettingActivity
import com.voiceassist.lixinyu.voiceassist.utils.FileUtils
import com.voiceassist.lixinyu.voiceassist.utils.JsonUtils
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils

import java.util.ArrayList
import java.util.concurrent.TimeUnit

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : BaseActivity(), EasyPermissions.PermissionCallbacks, IEmptyable {


    private var mLevel1ViewPager: ViewPager? = null
    private var mLevel2ViewPager: ViewPager? = null
    private var mLevel1NameTv: TextView? = null

    private var mLoadingDialog: LoadingDialog? = null

    private var mOnLevel1ItemClickListener: GridAdapter.OnItemClickListener? = null

    private var mLevel1PagerPointer: ViewPagerPointer? = null
    private var mLevel2PagerPointer: ViewPagerPointer? = null

    private var mExitConsumer: Consumer<Long>? = null
    private var mTempSettingConsumer: Consumer<Long>? = null
    private var mSettingDisposable: Disposable? = null

    private var mExitFlag = IDLE_FLAG
    private var mTempSettingFlag = IDLE_FLAG


    private var mTopEmptyViewStub: ViewStub? = null
    private var mBottomEmptyViewStub: ViewStub? = null

    private var relationshipList: List<Relationship>? = null
    private var secondLevelNodes: List<SecondLevelNode>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        initViews()
        initData()
        initListener()

        Beta.checkUpgrade(false, true)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)//这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_setting -> {
                startActivityForResult(Intent(this@MainActivity, SettingActivity::class.java), REQ_EDIT_DATA)
            }

            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun initViews() {
        mLevel1ViewPager = findViewById(R.id.main_viewpager_first_level)
        mLevel2ViewPager = findViewById(R.id.main_viewpager_second_level)
        mLevel1NameTv = findViewById(R.id.main_textview_first_level_name)

        mTopEmptyViewStub = findViewById(R.id.main_top_empty_viewstub)
        mBottomEmptyViewStub = findViewById(R.id.main_bottom_empty_viewstub)

        mLoadingDialog = LoadingDialog(this)

        mLevel1PagerPointer = findViewById(R.id.main_pager_indicator_first_level)
        mLevel2PagerPointer = findViewById(R.id.main_pager_indicator_second_level)
    }

    private fun initData() {
        getData()

        mExitConsumer = Consumer { mExitFlag = IDLE_FLAG }

        mTempSettingConsumer = Consumer {
            mSettingDisposable = null
            mTempSettingFlag = IDLE_FLAG
        }

    }

    private fun initListener() {
        mOnLevel1ItemClickListener = GridAdapter.OnItemClickListener { position, vo ->
            if (null != vo!!.node) {
                mLevel1NameTv!!.text = vo.node.cnName
            }

            secondLevelNodes = null
            if (null != vo && null != vo.relationship) {
                secondLevelNodes = vo.relationship.secondLevelNodes
            }

            val pagerViews = getPagers(secondLevelNodes)

            mLevel2ViewPager!!.adapter = MainPagerAdapter(pagerViews)
            mLevel2PagerPointer!!.setViewPager(mLevel2ViewPager)

            if (null == pagerViews || pagerViews.size <= 1) {
                mLevel2PagerPointer!!.visibility = View.INVISIBLE
            } else {
                mLevel2PagerPointer!!.visibility = View.VISIBLE
            }

            justifyDisplayEmptyView()
        }

        mLevel1NameTv!!.setOnClickListener {
            if (mTempSettingFlag < CAN_ENTER_FLAG) {
                mTempSettingFlag++
                if (mTempSettingFlag == CAN_ENTER_FLAG) {
                    // Enter
                    startActivityForResult(Intent(this@MainActivity, EditActivity::class.java), REQ_EDIT_DATA)
                } else {
                    if (null != mSettingDisposable) mSettingDisposable!!.dispose()
                    mSettingDisposable = Observable.timer(1, TimeUnit.SECONDS).subscribe(mTempSettingConsumer!!)
                    mRxManager.register(mSettingDisposable)
                }
            }
        }


    }


    private fun getData() {
        if (EasyPermissions.hasPermissions(this, *PERMS)) {
            getDataAsync()
        } else {
            EasyPermissions.requestPermissions(this, "需要您允许存储权限", RC_STORAGE_PERMISSION, *PERMS)
        }
    }


    private fun getDataAsync() {
        Observable.just(Constants.JSON_DATA_PATH)
                .map { filePath ->
                    val allData = getDataSync(filePath)

                    if (null != allData && null != allData.nodes) {
                        mNodesMap = ArrayMap()
                        for (node in allData.nodes) {
                            if (null == node) continue
                            mNodesMap?.put(node.id, node)
                        }
                    }

                    allData
                }.compose(RxHelper.rxSchedulerHelper())
                .subscribe(object : Observer<AllData?> {
                    override fun onSubscribe(d: Disposable) {
                        mRxManager.register(d)
                        mLoadingDialog!!.show()
                    }

                    override fun onNext(value: AllData?) {
                        renderView(value)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        mLoadingDialog!!.dismiss()
                    }

                    override fun onComplete() {
                        mLoadingDialog!!.dismiss()
                    }
                })
    }

    private fun getDataSync(filePath: String): AllData? {
        var sdCardStr: String? = null
        val rawStr = FileUtils.readRawNoSpace(AssistApplication.instance!!, R.raw.json_data)

        var rawAllData: AllData? = null
        var sdCardAllData: AllData? = null
        var allData: AllData? = null

        if (EasyPermissions.hasPermissions(this, *PERMS)) {
            sdCardStr = FileUtils.readFileSingleLine(filePath)

            if (null == sdCardStr) {
                FileUtils.copyFilesFromRawNoSpace(AssistApplication.instance, R.raw.json_data, filePath)
                sdCardStr = rawStr

                if (null != rawStr) return JsonUtils.getObjectFromJson(rawStr, AllData::class.java)
            }
        }

        if (null != rawStr) {
            rawAllData = JsonUtils.getObjectFromJson(rawStr, AllData::class.java)
            allData = rawAllData
        }

        if (null != sdCardStr) {
            sdCardAllData = JsonUtils.getObjectFromJson(sdCardStr, AllData::class.java)
            allData = sdCardAllData
        }

        if (null != rawAllData && null != sdCardAllData) {
            if (rawAllData.version > sdCardAllData.version) {
                allData = rawAllData
                FileUtils.copyFilesFromRawNoSpace(AssistApplication.instance, R.raw.json_data, filePath)
            } else {
                allData = sdCardAllData
            }
        }

        return allData
    }

    private fun renderView(allData: AllData?) {
        mAllData = allData

        relationshipList = null
        if (null != allData) relationshipList = allData.relationship

        // 渲染一级菜单
        val level1PagerViews = getPagers(relationshipList)
        mLevel1ViewPager!!.adapter = MainPagerAdapter(level1PagerViews)
        mLevel1PagerPointer!!.setViewPager(mLevel1ViewPager)

        // 渲染二级菜单
        secondLevelNodes = null
        mLevel2ViewPager!!.adapter = MainPagerAdapter(null)
        mLevel2PagerPointer!!.setViewPager(mLevel2ViewPager)

        // 初始化指示器
        mLevel1NameTv!!.text = "点击上边按钮发音"

        if (null == level1PagerViews || level1PagerViews.size <= 1) {
            mLevel1PagerPointer!!.visibility = View.INVISIBLE
        } else {
            mLevel1PagerPointer!!.visibility = View.VISIBLE
        }

        mLevel2PagerPointer!!.visibility = View.INVISIBLE

        justifyDisplayEmptyView()
    }

    private fun <T : INodeId> getPagers(noteIdList: List<T>?): ArrayList<View>? {
        if (null == noteIdList) return null

        val views = ArrayList<View>()
        var onePageData: MutableList<GridViewVo> = ArrayList()

        var nodeType = -1
        var onePageSize = -1

        for (nodeIdEntity in noteIdList) {
            if (null == nodeIdEntity) continue

            if (-1 == nodeType) nodeType = nodeIdEntity.nodeType

            onePageSize = onePageData.size

            // 在最一开始先生成ViewPager的一个新View，后续再给onePageData填数据进去
            if (views.size == 0 || onePageSize >= PAGE_SIZE) {
                if (onePageSize > 0) onePageData = ArrayList()

                var view: View? = null
                if (INodeId.NODE_TYPE_FIRST_LEVEL == nodeType) {
                    view = getLevel1OnePage(onePageData)
                } else if (INodeId.NODE_TYPE_SECOND_LEVEL == nodeType) {
                    view = getLevel2OnePage(onePageData)
                }
                if (null != view) views.add(view)

            }

            // 给onePageData加一个节点
            val node = mNodesMap?.get(nodeIdEntity.nodeId) ?: continue  // 节点为空则不加数据

            var relationship: Relationship? = null
            if (nodeIdEntity is Relationship) {
                relationship = nodeIdEntity
            }
            onePageData.add(GridViewVo(node, relationship))
        }// for循环结束

        val pageSize = views.size
        if (0 == onePageData.size && pageSize > 0) {
            views.removeAt(pageSize - 1)
        }

        return views
    }


    private fun getLevel1OnePage(data: List<GridViewVo>): View {
        val gridView = GridView(this)

        gridView.horizontalSpacing = 9
        gridView.verticalSpacing = 9
        gridView.numColumns = 3
        val adapter = GridAdapterFirstLevel(this, data, 3)
        adapter.setOnItemClickListener(mOnLevel1ItemClickListener)
        gridView.adapter = adapter

        return gridView
    }

    private fun getLevel2OnePage(data: List<GridViewVo>): View {
        val gridView = GridView(this)

        gridView.horizontalSpacing = 9
        gridView.verticalSpacing = 9
        gridView.numColumns = 3
        gridView.adapter = GridAdapterSecondLevel(this, data, 3)

        return gridView
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {

            if (mExitFlag == CAN_EXIT_FLAG) {
                finish()
                //System.exit(0);
            } else {
                mExitFlag = CAN_EXIT_FLAG
                mRxManager.register(Observable.timer(2, TimeUnit.SECONDS).subscribe(mExitConsumer!!))
                ToastUtils.showToast("再按一次退出应用")
            }

            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    // ========= API 23 permissions granted =========
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        when (requestCode) {
            RC_STORAGE_PERMISSION -> {
                getDataAsync()
            }
            else -> {
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        when (requestCode) {
            RC_STORAGE_PERMISSION -> {
                // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
                // This will display a dialog directing them to enable the permission in app settings.
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    AppSettingsDialog.Builder(this).build().show()// 用户选择『不开提示』时引导用户手动开启权限
                } else {
                    getDataAsync()
                }
            }
            else -> {
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when (requestCode) {
            AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE -> {// 用户手动开启权限后调用
                // Do something after user returned from app settings screen, like showing a Toast.
                getData()
            }

            REQ_EDIT_DATA -> {
                renderView(mAllData)
            }

            else -> {
            }
        }
    }

    override fun justifyDisplayEmptyView() {
        if (null == relationshipList || relationshipList!!.size == 0) {
            mTopEmptyViewStub!!.visibility = View.VISIBLE
        } else {
            mTopEmptyViewStub!!.visibility = View.GONE
        }

        if (null == secondLevelNodes || secondLevelNodes!!.size == 0) {
            mBottomEmptyViewStub!!.visibility = View.VISIBLE
        } else {
            mBottomEmptyViewStub!!.visibility = View.GONE
        }
    }

    companion object {

        private val TAG = "MainActivity"

        private val PAGE_SIZE = 9

        private val CAN_EXIT_FLAG = 0x100
        private val CAN_ENTER_FLAG = 2
        private val IDLE_FLAG = -1

        private val RC_STORAGE_PERMISSION = 0x100
        private val PERMS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        private val REQ_EDIT_DATA = 0x100

        var mAllData: AllData? = null
        var mNodesMap: ArrayMap<String, Node>? = null


        fun saveAllDatas() {
            Observable.create(ObservableOnSubscribe<Boolean> { emitter ->
                val jsonData = JsonUtils.getJsonFromObject(MainActivity.mAllData)
                val success = FileUtils.saveTextToFile(jsonData, Constants.JSON_DATA_PATH)

                emitter.onNext(success)
                //emitter.onComplete();
            }).compose(RxHelper.rxSchedulerHelper())
                    .subscribe { ToastUtils.showToast("编辑生效") }


        }
    }
}
