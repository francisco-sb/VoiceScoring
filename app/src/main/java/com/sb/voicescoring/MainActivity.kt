package com.sb.voicescoring

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    //region Recording
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()

        @Suppress("DEPRECATION")
        output = "${Environment.getExternalStorageDirectory().absolutePath}/recording.mp3"
        mediaRecorder = MediaRecorder()

        mediaRecorder?.let {
            it.setAudioSource(MediaRecorder.AudioSource.MIC)
            it.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            it.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            it.setOutputFile(output)
        }

        btn_microphone.setOnClickListener {
            if (!isRecording) {
                startRecording()
                btn_microphone.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_stop, null))
            } else {
                stopRecording()
                btn_microphone.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_microphone, null))
            }
        }
    }

    //region:: PRIVATE METHODS
    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        }
    }

    private fun startRecording() {
        try {
            mediaRecorder?.let {
                it.prepare()
                it.start()
                isRecording = true
                Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            mediaRecorder?.let {
                it.stop()
                it.release()
                isRecording = false
            }
        } else {
            Toast.makeText(this, "You are not recording!", Toast.LENGTH_SHORT).show()
        }
    }
    //endregion
}