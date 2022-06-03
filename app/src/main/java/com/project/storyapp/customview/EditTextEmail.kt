package com.project.storyapp.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.project.storyapp.R

class EditTextEmail : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                error = if (text.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(text.toString())
                        .matches()
                )
                    context.getString(R.string.email_invalid) else null
            }

            override fun afterTextChanged(p0: Editable?) {
                // Do nothing
            }

        })
    }

}