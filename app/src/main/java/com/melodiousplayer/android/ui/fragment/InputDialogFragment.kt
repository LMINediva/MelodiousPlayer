package com.melodiousplayer.android.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.InputDialogListener

/**
 * 输入数据的弹窗
 */
class InputDialogFragment : DialogFragment() {
    private lateinit var editText: EditText
    private var inputListener: InputDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dialog_input, null)
        editText = view.findViewById(R.id.editTextDialogUserInput)
        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
            .setTitle("请输入服务器IP地址：")
            .setPositiveButton("确定") { _, _ ->
                inputListener?.onFinishEdit(editText.text.toString())
            }
            .setNeutralButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is InputDialogListener) {
            inputListener = context
        } else {
            throw RuntimeException("${context}必须实现InputDialogListener接口")
        }
    }

    override fun onDetach() {
        inputListener = null
        super.onDetach()
    }

}