package com.sb.voicescoring

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()

        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)

        val pdh = PitchDetectionHandler { pitchDetectionResult, event ->
            val pitchInHz = pitchDetectionResult.pitch
            runOnUiThread {
                /*if (pitchDetectionResult.isPitched) {
                    Log.e("PitchDetection", "Pitch: ${pitchDetectionResult.pitch} - Probability: ${pitchDetectionResult.probability}")
                    Log.e("PitchDetection", "MIDI: ${processMIDI(pitchInHz)}")
                    Log.e("AudioEvent", "Start Time: ${event.timeStamp} | End Time: ${event.endTimeStamp}")
                    Log.e("AudioEvent", "Frame length: ${event.frameLength}")
                }*/
                processPitch(pitchInHz)
                Log.e("PitchDetection", "Pitch: $pitchInHz - Is silence: ${event.isSilence(pitchInHz.toDouble())}")
            }
        }

        val pitchProcessor = PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050f, 1024, pdh)

        dispatcher.addAudioProcessor(pitchProcessor)

        val audioThread = Thread(dispatcher, "Audio Thread")
        audioThread.start()
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

    private fun processPitch(pitchInHz: Float) {
        val pitch = "Pitch in Hz: $pitchInHz"
        txt_pitch.text = pitch

        var note = "Note: "
        when {
            pitchInHz >= 16.35 && pitchInHz < 18.35 -> note += "C0"
            pitchInHz >= 18.35 && pitchInHz < 20.60 -> note += "D0"
            pitchInHz >= 20.60 && pitchInHz < 21.83 -> note += "E0"
            pitchInHz >= 21.83 && pitchInHz < 24.50 -> note += "F0"
            pitchInHz >= 24.50 && pitchInHz < 27.50 -> note += "G0"
            pitchInHz >= 27.50 && pitchInHz < 30.87 -> note += "A0"
            pitchInHz >= 30.87 && pitchInHz < 32.70 -> note += "B0"
            pitchInHz >= 32.70 && pitchInHz < 36.71 -> note += "C1"
            pitchInHz >= 36.71 && pitchInHz < 41.20 -> note += "D1"
            pitchInHz >= 41.20 && pitchInHz < 43.65 -> note += "E1"
            pitchInHz >= 43.65 && pitchInHz < 49.00 -> note += "F1"
            pitchInHz >= 49.00 && pitchInHz < 55.00 -> note += "G1"
            pitchInHz >= 55.00 && pitchInHz < 61.74 -> note += "A1"
            pitchInHz >= 61.74 && pitchInHz < 65.41 -> note += "B1"
            pitchInHz >= 65.41 && pitchInHz < 73.42 -> note += "C2"
            pitchInHz >= 73.42 && pitchInHz < 82.41-> note += "D2"
            pitchInHz >= 82.41 && pitchInHz < 87.31-> note += "E2"
            pitchInHz >= 87.31 && pitchInHz < 98.00-> note += "F2"
            pitchInHz >= 98.00 && pitchInHz < 110.00-> note += "G2"
            pitchInHz >= 110.00 && pitchInHz < 123.47-> note += "A2"
            pitchInHz >= 123.47 && pitchInHz < 130.81-> note += "B2"
            pitchInHz >= 130.81 && pitchInHz < 146.83-> note += "C3"
            pitchInHz >= 146.83 && pitchInHz < 164.81-> note += "D3"
            pitchInHz >= 164.81 && pitchInHz < 174.61-> note += "E3"
            pitchInHz >= 174.61 && pitchInHz < 196.00-> note += "F3"
            pitchInHz >= 196.00 && pitchInHz < 220.00-> note += "G3"
            pitchInHz >= 220.00 && pitchInHz < 246.94-> note += "A3"
            pitchInHz >= 246.94 && pitchInHz < 261.63-> note += "B3"
            pitchInHz >= 261.63 && pitchInHz < 293.66-> note += "C4"
            pitchInHz >= 293.66 && pitchInHz < 329.63-> note += "D4"
            pitchInHz >= 329.63 && pitchInHz < 349.23-> note += "E4"
            pitchInHz >= 349.23 && pitchInHz < 392.00-> note += "F4"
            pitchInHz >= 392.00 && pitchInHz < 440.00-> note += "G4"
            pitchInHz >= 440.00 && pitchInHz < 493.88-> note += "A4"
            else -> note += "Out of range"
        }

        txt_note.text = note
    }
    //endregion
}