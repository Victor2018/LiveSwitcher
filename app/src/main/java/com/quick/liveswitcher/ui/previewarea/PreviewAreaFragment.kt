package com.quick.liveswitcher.ui.previewarea

import android.graphics.Outline
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.fragment.app.Fragment
import com.quick.liveswitcher.R
import com.quick.liveswitcher.data.scene.SceneBean
import com.quick.liveswitcher.databinding.FragmentPreviewAreaBinding
import com.quick.liveswitcher.local.FileManager
import com.quick.liveswitcher.ui.bottomfragment.scene.SceneDataManager
import com.quick.liveswitcher.utils.FileUtil
import com.quick.liveswitcher.utils.ResUtils
import com.tripod.bls3588.video.Vcamera
import sdk.smartx.director.SXSLayerApi
import sdk.smartx.director.layer.IRenderLayer


class PreviewAreaFragment : Fragment() {
    var mLayerApi: SXSLayerApi? = null
    private lateinit var binding: FragmentPreviewAreaBinding
    private val FPS = 60
    private var mGestureView: MaterialGestureLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dir: String = FileUtil.getPresetMaterialDir(activity) + "license"
        mLayerApi = SXSLayerApi(activity, dir, false)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreviewAreaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        regEvent()
    }

    private fun initView() {
        binding.textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                Log.i(
                    "TAG", "onSurfaceTextureAvailable: width: " + width + " height: " +
                            height
                );
                surface.setDefaultBufferSize(width, height)

                LayerController.init(
                    this@PreviewAreaFragment,
                    mLayerApi,
                    binding.textureView.width,
                    binding.textureView.height
                )

                //1.初始化预览渲染窗口//
                mLayerApi!!.initCanvas(surface, Size(width, height), Size(height, width))
                mLayerApi!!.setRenderFPS(FPS)
//                mLayerApi!!.muteSpeaker(mPref.getBoolean("audiomute", false))

                SceneDataManager.initSceneData(this@PreviewAreaFragment)
                addGestureView()
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                mLayerApi?.freeCanvas()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }

        }

        // 图层显示区域圆角
        binding.textureView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                // 设置圆角半径
                val cornerRadius = ResUtils.getDimenFloatPixRes(R.dimen.dp_26) // 圆角半径，单位：px
                outline!!.setRoundRect(0, 0, view!!.width, view.height, cornerRadius)
            }

        }
        binding.textureView.clipToOutline = true

        initEvent()
    }

    private fun initEvent(){
       mLayerApi?.setDataOutputListener { format, width, height, data, buffer ->

           val params = intArrayOf(0, 0)
           Vcamera.send(data, width, height, params)

       }
    }

    fun setEditEnable(editEnable: Boolean) {
        mGestureView?.setEditEnable(editEnable)
    }

    private fun regEvent() {

    }

    //预览框截图
    fun toPreviewScreenShot(sceneBean: SceneBean) {
        FileManager.savePreviewShotBitmap(binding.textureView.bitmap, sceneBean)
    }

    private fun addGestureView() {
        val editor: View? = binding.touchLayout.findViewWithTag<View>("mode_editor")
        if (editor != null) {
            if (mGestureView != null) {
                mGestureView?.setOnPositionChangedListener(null)
                mGestureView?.forceFinish()
            }
            mGestureView = null
            binding.touchLayout.removeView(editor)
        }
        mGestureView =
            MaterialGestureLayout(context)
        mGestureView?.setTag("mode_editor")
        binding.touchLayout.addView(
            mGestureView,
            0,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mGestureView?.setKeepRatio(false)
        mGestureView?.setTouchOutsideEnable(true)
        //mGestureView.setBackgroundColor(Color.argb(0x88, 0x00, 0x33, 0x00));
        mGestureView?.setOnPositionChangedListener(mPositionListener)
        mGestureView?.setOutCallback(object : MaterialGestureLayout.OutCallabck {
            override fun isLayerVisibleById(layerId: Int): Boolean {
                val list = mLayerApi!!.layers
                for (i in list.indices) {
                    if (layerId == list[i].layerId) {
                        return list[i].config.isVisible
                    }
                }
                return false
            }

            override fun getLayerRectF(layerId: Int): RectF {
                val list = mLayerApi!!.layers
                for (i in list.indices) {
                    if (layerId == list[i].layerId) {
                        return list[i].rect
                    }
                }
                return RectF()
            }

            override fun findClosestCoordLayer(x: Float, y: Float): Int {
                var layerId = -1
                val list = mLayerApi!!.layers

                val sortedList = list.sortedByDescending { mLayerApi!!.getLayerOrder(it.layerId) }

                for (layer in sortedList) {
                    val rect = layer.rect
                    if (rect.contains(x, y)) {
                        layerId = layer.layerId
                        break;
                    }
                }
                return layerId
            }

            override fun getLayerById(layerId: Int): IRenderLayer? {
                val list = mLayerApi!!.layers
                for (i in list.indices) {
                    if (layerId == list[i].layerId) {
                        return list[i]
                    }
                }
                return null
            }

        })
    }

    private val mPositionListener: MaterialGestureLayout.OnPositionChangedListener =
        object : MaterialGestureLayout.OnPositionChangedListener {
            override fun onSelected(id: Int) {
                LayerController.onLayerSelected(id)
            }

            override fun onDataChanged(id: Int, rectF: RectF?) {
                if (id != -1) {
                    val list = mLayerApi?.getLayers()
                    if (list != null) {
                        for (layer in list) {
                            if (id == layer.layerId) {
                                layer.updateConfig(mapOf("setPosition" to rectF))
                                break
                            }
                        }
                    }

                }
                // 当前正在拖动的view的id和位置
//                Log.e("TAG","onDataChanged:layerId:$id  rectF:$rectF")
            }

            override fun onFinish(id: Int, rectF: RectF?) {
                // 拖动完成后的view的id和位置
                Log.e("tag","onFinish:layerId:$id  rectF:$rectF")
            }

            override fun onTouchOutside() {
                LayerController.onLayerUnSelected()
            }

            override fun onTimerEnd(){
                Log.e("tag","onTimerEnd")
                LayerController.notifySelectedLayerBeanRectChange()
                SceneDataManager.updateDbScene(SceneDataManager.getCurrentScene())
            }

        }

    //当前选择的图层被移除了
    fun currentSelectedLayerRemove() {
        mGestureView?.onFinished(true)
    }

    fun onLayerSelected(layerId: Int) {
//        val LayerSettingView: View = binding.root.findViewById<View>(R.id.layer_setting)
//        LayerSettingView.setTag(R.id.view_index_tag, layerId)
//        val view = LayerSettingView.findViewById<TextView>(R.id.layer_name)
//        view.setText(fragment.getLayerName(layerId))
//        val v = LayerSettingView.findViewById<ImageView>(R.id.iv_visible)
//        v.isSelected = fragment.getLayerVisible(layerId)
//
//        LayerSettingView.visibility = View.VISIBLE
//        mRoot.findViewById<View>(R.id.keycut_setting).setVisibility(View.INVISIBLE)
//        mRoot.findViewById<View>(R.id.beauty_setting_ll).setVisibility(View.INVISIBLE)

        // LayerSettingView.findViewById(R.id.beauty_sharp).setVisibility(View.GONE);
        // LayerSettingView.findViewById(R.id.beauty_smooth).setVisibility(View.GONE);
        // LayerSettingView.findViewById(R.id.beauty_whiten).setVisibility(View.GONE);
        // LayerSettingView.findViewById(R.id.beauty_light).setVisibility(View.GONE);
    }

}