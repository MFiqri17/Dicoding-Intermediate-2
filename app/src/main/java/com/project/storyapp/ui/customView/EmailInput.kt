package com.project.storyapp.ui.customView
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import java.util.regex.Pattern


class EmailInput : AppCompatEditText {

    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
    )
    private val ERROR_COLOR = Color.RED

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize()
    }

    private fun initialize() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                val email = s?.toString() ?: ""
                if (!isEmailValid(email)) {
                    showError()
                } else {
                    hideError()
                }
            }
        })
    }

    private fun isEmailValid(email: String): Boolean {
        return EMAIL_PATTERN.matcher(email).matches()
    }

    private fun showError() {
        // Set the error message or display error indication as per your UI design
        error = "Invalid email format"
        setTextColor(ERROR_COLOR)
    }

    private fun hideError() {
        error = null
        val textColor: Int
        if (isDarkTheme()) {
            textColor = android.R.color.white
        } else {
            textColor = android.R.color.black
        }
        setTextColor(ContextCompat.getColor(context, textColor))
    }

    private fun isDarkTheme(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}