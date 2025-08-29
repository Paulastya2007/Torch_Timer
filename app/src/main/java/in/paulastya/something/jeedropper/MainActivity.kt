package `in`.paulastya.something.jeedropper

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
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


    private fun toggleTorch(isTorchOn: Boolean) {
        try {
            val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0] // Assuming back camera has flash
                cameraManager.setTorchMode(cameraId, isTorchOn)

        } catch (e: CameraAccessException) {
            e.printStackTrace()
            // Handle the exception (e.g., display a Toast message if no flashlight is found)
            Toast.makeText(this, "No flashlight found or error accessing camera.", Toast.LENGTH_SHORT).show()
        }
    }

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

        val txtHello = findViewById<TextView>(R.id.textView)
        val edtName = findViewById<EditText>(R.id.editTextNumberSigned)
        val toggleButton = findViewById<ToggleButton>(R.id.toggleButton)


        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getInt("saved_number", 0)
        if (savedNumber != -1) {
            if (savedNumber !=0){
                edtName.setText(savedNumber.toString())
            }

        }

        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //torch on

                val time = edtName.text.toString().toIntOrNull() ?: 0
                txtHello.visibility= View.VISIBLE
                val conc= "countdown started $time"
                toggleTorch(true)
                sharedPreferences.edit {
                    putInt("saved_number", time)
                }
                txtHello.text=conc
                val times=time*1000
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    toggleTorch(false)
                    toggleButton.setChecked(false)


                }, times.toLong())
            }
else{
                txtHello.visibility= View.INVISIBLE
                toggleTorch(false)

}
        }

    }




}