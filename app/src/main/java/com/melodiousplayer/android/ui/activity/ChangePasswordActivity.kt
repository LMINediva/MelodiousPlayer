package com.melodiousplayer.android.ui.activity

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.ChangePasswordContract
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.presenter.impl.ChangePasswordPresenterImpl
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 修改用户登录密码界面
 */
class ChangePasswordActivity : BaseActivity(), ToolBarManager, View.OnClickListener,
    ChangePasswordContract.View {

    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var updatePasswordButton: Button
    private lateinit var token: String
    private lateinit var currentUser: UserBean
    private val changePasswordPresenter = ChangePasswordPresenterImpl(this)

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_change_password
    }

    override fun initData() {
        initChangePasswordToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        oldPassword = findViewById(R.id.oldPassword)
        newPassword = findViewById(R.id.newPassword)
        confirmPassword = findViewById(R.id.confirmPassword)
        updatePasswordButton = findViewById(R.id.updatePassword)
        val userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            currentUser = userSerialized as UserBean
        }
        // 从SharedPreferences文件中读取token的值
        token = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("token", "").toString()
    }

    override fun initListener() {
        updatePasswordButton.setOnClickListener(this)
    }

    /**
     * Toolbar上的图标按钮点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.updatePassword -> {
                if (token.isNotEmpty()) {
                    currentUser.oldPassword = oldPassword.text.trim().toString()
                    currentUser.newPassword = newPassword.text.trim().toString()
                    currentUser.updateTime = null
                    currentUser.createTime = null
                    val confirmPassword = confirmPassword.text.trim().toString()
                    changePasswordPresenter.changePassword(token, currentUser, confirmPassword)
                }
            }
        }
    }

    override fun onOldPasswordError() {
        myToast(getString(R.string.password_error))
        oldPassword.error = getString(R.string.password_error)
    }

    override fun onNewPasswordError() {
        myToast(getString(R.string.password_error))
        newPassword.error = getString(R.string.password_error)
    }

    override fun onConfirmPasswordError() {
        myToast(getString(R.string.password_error))
        confirmPassword.error = getString(R.string.password_error)
    }

    override fun onTwoPasswordsNotMatchError() {
        myToast(getString(R.string.two_passwords_not_match))
        confirmPassword.error = getString(R.string.two_passwords_not_match)
    }

    override fun onChangePasswordSuccess() {
        myToast(getString(R.string.change_password_success))
    }

    override fun onChangePasswordFailed(result: ResultBean) {
        result.msg?.let { myToast(it) }
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}