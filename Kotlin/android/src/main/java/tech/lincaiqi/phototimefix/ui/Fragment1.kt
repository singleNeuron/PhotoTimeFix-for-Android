package tech.lincaiqi.phototimefix.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import tech.lincaiqi.phototimefix.Core
import tech.lincaiqi.phototimefix.R
import tech.lincaiqi.phototimefix.databinding.Fragment1Binding
import tech.lincaiqi.phototimefix.utils.freshMedia
import tech.lincaiqi.phototimefix.utils.initFragment
import tech.lincaiqi.phototimefix.utils.updateAppbar

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
        preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()
        locateTv = binding.locateText
        locateText = binding.locateText
        binding.chooseButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            }
            startActivityForResult(intent, this@Fragment1.hashCode() ushr 16)
        }
        radioGroup = binding.radioGroup
        initFragment(preferences, editor, radioGroup)
        binding.startButton.setOnClickListener {
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
        binding.freshButton.setOnClickListener {
            val fileString: String = binding.locateText.text.toString()
            freshMedia(fileString, requireActivity())
        }
        return binding.root
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var path = resultSolve(requireContext(), requestCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (path != "error") {
            path = path.substring(0, path.lastIndexOf("/"))
            locateTv.setText(path)
            editor.putString("locate", path)
            editor.apply()
        } else context!!.longToast(getString(R.string.selectError))
    }*/

    override fun onResume() {
        super.onResume()
        val context = context
        if (context != null) {
            initFragment(preferences, editor, radioGroup)
            updateAppbar(requireActivity(), true)
            /* 作者：Silas_
            来源：CSDN
            原文：https://blog.csdn.net/qq_31852701/article/details/80859644
            版权声明：本文为博主原创文章，转载请附上博文链接！ */
        }
    }

}