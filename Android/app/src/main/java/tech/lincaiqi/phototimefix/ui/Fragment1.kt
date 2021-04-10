package tech.lincaiqi.phototimefix.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import tech.lincaiqi.phototimefix.*
import tech.lincaiqi.phototimefix.databinding.Fragment1Binding
import tech.lincaiqi.phototimefix.utils.*

class Fragment1 : Fragment() {

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var core = Core()
    private lateinit var locateTv: EditText
    private lateinit var locateText: EditText
    private lateinit var chooseBtn: Button
    private lateinit var radioGroup: RadioGroup

    private lateinit var binding: Fragment1Binding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, parent, savedInstanceState)
        binding = Fragment1Binding.inflate(inflater, parent, false)
        preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()
        locateTv = binding.locateText
        locateText = binding.locateText
        locateText.setText(preferences.getString("locate", Environment.getExternalStorageDirectory().path + "/DCIM/Camera"))
        chooseBtn = binding.chooseButton
        radioGroup = binding.radioGroup
        initFragment(requireContext(), preferences, editor, chooseBtn, radioGroup, this, true)
        val startBtn = binding.startButton
        startBtn.setOnClickListener {
            val startNum: Int = Integer.valueOf(binding.start.text.toString())
            val endNum: Int = Integer.valueOf(binding.end.text.toString())
            val fileString: String = binding.locateText.text.toString()
            val editFormat = binding.editFormat
            val format: String = if (editFormat.text.toString() == "") "yyyyMMddHHmm" else editFormat.text.toString()
            val radio: Boolean = radioGroup.checkedRadioButtonId == R.id.radioButton
            Log.d("radio", radioGroup.checkedRadioButtonId.toString())
            Log.d("radio", R.id.radioButton.toString())
            core.process(context, startNum, endNum, fileString, radio, activity, format, "", preferences.getInt("delay", 0), preferences.getBoolean("useEXIF", false))
        }
        val freshButton = binding.freshButton
        freshButton.setOnClickListener {
            val fileString: String = binding.locateText.text.toString()
            freshMedia(fileString, context!!)
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var path = resultSolve(requireContext(), requestCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (path != "error") {
            path = path.substring(0, path.lastIndexOf("/"))
            locateTv.setText(path)
            editor.putString("locate", path)
            editor.apply()
        } else context!!.longToast(getString(R.string.selectError))
    }

    override fun onResume() {
        super.onResume()
        val context = context
        if (context != null) {
            initFragment(context, preferences, editor, chooseBtn, radioGroup, this, true)
            updateAppbar(activity!!, true)
            /* 作者：Silas_
            来源：CSDN
            原文：https://blog.csdn.net/qq_31852701/article/details/80859644
            版权声明：本文为博主原创文章，转载请附上博文链接！ */
        }
    }

}