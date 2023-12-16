package edu.uw.ischool.nalogman.arewethereyet

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
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
    private lateinit var phoneNumber: String
    private lateinit var messageText: String

    private val sendSmsRunnable = object : Runnable {
        override fun run() {
            if (isServiceStarted) {
                sendSms(phoneNumber, messageText)
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

        // Save phone number and message text
        phoneNumber = phoneNumberEditText.text.toString()
        messageText = messageEditText.text.toString()

        // Check for SMS permission
        if (checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, schedule the first SMS
            handler.post(sendSmsRunnable)
        } else {
            // Permission not granted, request it
            requestPermissions(arrayOf(android.Manifest.permission.SEND_SMS), REQUEST_SMS_PERMISSION)
        }
    }

    private fun sendSms(phoneNumber: String, message: String) {
        // Send SMS
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    private fun stopService() {
        // Set the button text to "Start"
        startStopButton.text = getString(R.string.start)

        // Flag the service as stopped
        isServiceStarted = false

        // Remove any pending posts of the SMS sending runnable from the handler
        handler.removeCallbacks(sendSmsRunnable)
    }

    companion object {
        private const val REQUEST_SMS_PERMISSION = 1
    }
}