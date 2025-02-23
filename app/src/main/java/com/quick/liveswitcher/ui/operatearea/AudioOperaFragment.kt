package com.quick.liveswitcher.ui.operatearea

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.FragmentOperaAreaAudioBinding
import com.quick.liveswitcher.utils.SharedPreferencesUtils
import com.quick.liveswitcher.utils.ViewUtils.hide
import com.quick.liveswitcher.utils.ViewUtils.show
import com.tripod.bls3588.audio.Vaudio

class AudioOperaFragment : Fragment(), OnSeekBarChangeListener, OnCheckedChangeListener {
    companion object {

        fun newInstance(): AudioOperaFragment {
            val fragment = AudioOperaFragment()
            return fragment
        }
    }

    lateinit var binding: FragmentOperaAreaAudioBinding
    lateinit var mVaudio: Vaudio
    val scene_in_toggle_key = "scene_in_toggle_key"
    val scene_in_progress_key = "scene_in_progress_key"
    val out_speaker_toggle_key = "out_speaker_toggle_key"
    val out_speaker_val_key = "out_speaker_val_key"
    val room_val_key = "room_val_key"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOperaAreaAudioBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVaudio = Vaudio.getInstance(this.context);
        initView()
    }

    fun initView() {
        binding.mSbSceneVol.setOnSeekBarChangeListener(this)
        binding.mSbOutVol.setOnSeekBarChangeListener(this)
        binding.mSbLiveVol.setOnSeekBarChangeListener(this)
        binding.mToggleSceneVal.setOnCheckedChangeListener(this)
        binding.mToggleOutVal.setOnCheckedChangeListener(this)

        binding.mToggleSceneVal.isChecked = getSceneInToggleValue()
        binding.mToggleOutVal.isChecked = getOutSpeakerToggleValue()

        binding.mSbSceneVol.progress = getSceneInProgressValue()
        binding.mSbOutVol.progress = getOutSpeakerProgressValue()
        binding.mSbLiveVol.progress = getRoomValProgress()

        changeMode()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        var volume = progress.toFloat()
        volume /= 100f

        when (seekBar?.id) {
            R.id.mSbSceneVol -> {
                binding.mTvSceneVol.text = "$progress%"
                mVaudio.setHwAudioRecordVolume(volume)
                setSceneInProgressValue(progress)
            }

            R.id.mSbOutVol -> {
                binding.mTvOutVol.text = "$progress%"
                mVaudio.setHwAudioPlayerVolume(volume)
                setOutSpeakerProgressValue(progress)
            }

            R.id.mSbLiveVol -> {
                binding.mTvLiveVol.text = "$progress%"
                mVaudio.setHookAudioPlayerVolume(volume)
                setRoomValProgress(progress)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mToggleSceneVal -> {
                if (isChecked) {
                    binding.mSbSceneVol.show()
                    binding.mTvSceneVol.show()
                } else {
                    binding.mSbSceneVol.hide()
                    binding.mTvSceneVol.hide()
                }
               setSceneInToggleValue(isChecked)
            }

            R.id.mToggleOutVal -> {
                if (isChecked) {
                    binding.mSbOutVol.show()
                    binding.mTvOutVol.show()
                } else {
                    binding.mTvOutVol.hide()
                    binding.mSbOutVol.hide()
                }
                setOutSpeakerToggleValue(isChecked)
            }
        }
        changeMode()
    }

    fun changeMode() {
        val sceneValChecked = binding.mToggleSceneVal.isChecked;
        val outValChecked = binding.mToggleOutVal.isChecked;
        if (sceneValChecked && outValChecked) {
            mVaudio.setHelperMode(0)
        } else if (sceneValChecked && !outValChecked) {
            mVaudio.setHelperMode(4)
        } else if (!sceneValChecked && outValChecked) {
            mVaudio.setHelperMode(3)
        } else if (!sceneValChecked && !outValChecked) {
            mVaudio.setHelperMode(7)
        }
    }

    fun getSceneInToggleValue(): Boolean {
        return SharedPreferencesUtils.getBoolean(scene_in_toggle_key, true)
    }

    fun setSceneInToggleValue(scene_in_toggle: Boolean) {
        SharedPreferencesUtils.putBoolean(scene_in_toggle_key, scene_in_toggle)
    }

    fun getSceneInProgressValue(): Int {
        return SharedPreferencesUtils.getInt(scene_in_progress_key, 80)
    }

    fun setSceneInProgressValue(scene_in_progress: Int) {
        SharedPreferencesUtils.putInt(scene_in_progress_key, scene_in_progress)
    }

    fun getOutSpeakerToggleValue(): Boolean {
        return SharedPreferencesUtils.getBoolean(out_speaker_toggle_key, true)
    }

    fun setOutSpeakerToggleValue(out_speaker_toggle: Boolean) {
        SharedPreferencesUtils.putBoolean(out_speaker_toggle_key, out_speaker_toggle)
    }

    fun getOutSpeakerProgressValue(): Int {
        return SharedPreferencesUtils.getInt(out_speaker_val_key, 80)
    }

    fun setOutSpeakerProgressValue(out_speaker_progress: Int) {
        SharedPreferencesUtils.putInt(out_speaker_val_key, out_speaker_progress)
    }

    fun getRoomValProgress(): Int {
        return SharedPreferencesUtils.getInt(room_val_key, 80)
    }

    fun setRoomValProgress(roomValProgress: Int) {
        SharedPreferencesUtils.putInt(room_val_key, roomValProgress)
    }


}