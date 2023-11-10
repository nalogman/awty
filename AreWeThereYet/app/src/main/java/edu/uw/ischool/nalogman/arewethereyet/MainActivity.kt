package edu.uw.ischool.nalogman.arewethereyet

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var messageEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var minutesEditText: EditText
    private lateinit var startStopButton: Button
    private var isServiceStarted = false
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (isServiceStarted) {
                Toast.makeText(
                    this@MainActivity,
                    "${phoneNumberEditText.text}: ${messageEditText.text}",
                    Toast.LENGTH_SHORT
                ).show()
                handler.postDelayed(this, minutesEditText.text.toString().toLong() * 60000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize your UI components here
        messageEditText = findViewById(R.id.editTextMessage)
        phoneNumberEditText = findViewById(R.id.editTextPhoneNumber)
        minutesEditText = findViewById(R.id.editTextMinutes)
        startStopButton = findViewById(R.id.buttonStartStop)

        startStopButton.setOnClickListener {
            if (isServiceStarted) {
                stopService()
            } else {
                if (validateInputs()) {
                    startService()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val message = messageEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()
        val minutesStr = minutesEditText.text.toString()

        return when {
            message.isEmpty() -> {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
                false
            }
            phoneNumber.isEmpty() -> {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
                false
            }
            minutesStr.isEmpty() -> {
                Toast.makeText(this, "Please enter the minutes interval", Toast.LENGTH_SHORT).show()
                false
            }
            minutesStr.toIntOrNull() ?: -1 <= 0 -> {
                Toast.makeText(this, "Minutes must be a positive integer", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun startService() {
        // Set the button text to "Stop"
        startStopButton.text = getString(R.string.stop)

        // Flag the service as started
        isServiceStarted = true

        // Schedule the first Toast
        handler.post(runnable)
    }

    private fun stopService() {
        // Set the button text to "Start"
        startStopButton.text = getString(R.string.start)

        // Flag the service as stopped
        isServiceStarted = false

        // Remove any pending posts of the runnable from the handler
        handler.removeCallbacks(runnable)
    }
}