package com.melodiousplayer.android.ui.activity

import android.text.Editable
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.util.DateUtil
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.URLProviderUtils
import de.hdodenhof.circleimageview.CircleImageView

/**
 * 个人信息界面
 */
class UserInfoActivity : BaseActivity(), ToolBarManager {

    private lateinit var avatarImage: CircleImageView
    private lateinit var username: EditText
    private lateinit var phonenumber: EditText
    private lateinit var email: EditText
    private lateinit var role: TextView
    private lateinit var createTime: TextView

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_info
    }

    override fun initData() {
        initUserInfoToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        avatarImage = findViewById(R.id.userAvatar)
        username = findViewById(R.id.userName)
        phonenumber = findViewById(R.id.phonenumber)
        email = findViewById(R.id.email)
        role = findViewById(R.id.role)
        createTime = findViewById(R.id.createTime)
        val userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            val currentUser = userSerialized as UserBean
            username.text = Editable.Factory.getInstance().newEditable(currentUser.username)
            phonenumber.text = Editable.Factory.getInstance().newEditable(currentUser.phonenumber)
            email.text = Editable.Factory.getInstance().newEditable(currentUser.email)
            role.text = currentUser.roles
            createTime.text = currentUser.createTime?.let { DateUtil.formatDateToString(it) }
            Glide.with(this).load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.userAvatarPath + currentUser.avatar
            ).into(avatarImage)
        }
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

}