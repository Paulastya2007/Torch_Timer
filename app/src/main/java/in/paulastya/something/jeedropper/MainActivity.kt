package `in`.paulastya.something.jeedropper

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.view.View

import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var countDownTimer: CountDownTimer? = null

    private lateinit var statusTextView: TextView
    private lateinit var timeInput: EditText
    private lateinit var toggleButton: ToggleButton



    private var previousValue: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        statusTextView = findViewById(R.id.textView)
        timeInput = findViewById(R.id.editTextNumberSigned)
        toggleButton = findViewById(R.id.toggleButton)

        // Initialize camera manager and get camera ID
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            Toast.makeText(this, "No flashlight found.", Toast.LENGTH_SHORT).show()
        }

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getInt("saved_number", -1)
        if (savedNumber != -1) {
            timeInput.setText(savedNumber.toString())
        }

        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val timeInSeconds = timeInput.text.toString().toIntOrNull()
                if (timeInSeconds == null || timeInSeconds <= 0) {
                    Toast.makeText(this, "Please enter a valid time in seconds.", Toast.LENGTH_SHORT).show()
                    toggleButton.isChecked = false
                    return@setOnCheckedChangeListener
                }

                // Save the valid time to SharedPreferences
                sharedPreferences.edit { putInt("saved_number", timeInSeconds) }

                startCountdownAndTorch(timeInSeconds.toLong())
            } else {
                stopCountdownAndTorch()
            }
        }

    }
    private fun startCountdownAndTorch(seconds: Long) {
        statusTextView.visibility = View.VISIBLE
        toggleTorch(true)

        countDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = millisUntilFinished / 1000
                statusTextView.text = "Timer running: $remainingSeconds seconds remaining"
            }

            override fun onFinish() {
                stopCountdownAndTorch()
                toggleButton.isChecked = false // Set the toggle button to 'off' state
                statusTextView.text = "Timer finished!"
            }
        }.start()
    }

    private fun stopCountdownAndTorch() {
        countDownTimer?.cancel()
        toggleTorch(false)
        statusTextView.visibility = View.INVISIBLE
    }

    private fun toggleTorch(isTorchOn: Boolean) {
        if (cameraId == null) return

        try {
            cameraManager.setTorchMode(cameraId!!, isTorchOn)
        } catch (e: CameraAccessException) {
            Toast.makeText(this, "Error accessing camera.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel() // Ensure the timer is cancelled to prevent memory leaks
        toggleTorch(false)
    }





}