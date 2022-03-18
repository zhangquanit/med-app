package com.medlinker.hybridapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.medlinker.hybridapp.entity.UserInfoEntity

class LoginActivity : AppCompatActivity() {

    companion object {
        var userInfo: UserInfoEntity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<View>(R.id.btnLogin).setOnClickListener {
            val et = findViewById<EditText>(R.id.etName).text.toString()
            userInfo = UserInfoEntity(et)
            setResult(RESULT_OK)
            finish()
        }
    }
}