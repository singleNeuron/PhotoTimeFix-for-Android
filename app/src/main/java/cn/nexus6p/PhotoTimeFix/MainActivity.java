package cn.nexus6p.PhotoTimeFix;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private TextView locateTv;
    private EditText start;
    private EditText end;
    private RadioGroup radioGroup;
    private EditText editFormat;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    boolean support = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();

        if (preferences.getBoolean("iffirst",true)) {
            showAbout();
            editor.putBoolean("iffirst",false);
            editor.apply();
        }

        Button startBtn = (Button) findViewById(R.id.startbutton);
        Button chooseBtn = (Button) findViewById(R.id.chooseButton);
        locateTv = (TextView) findViewById(R.id.locateText);
        start = (EditText) findViewById(R.id.start);
        end = (EditText) findViewById(R.id.end);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        editFormat = (EditText) findViewById(R.id.editFormat);

        locateTv.setText(preferences.getString("locate", Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera"));
        radioGroup.check(preferences.getInt("mode",R.id.radioButton));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                editor.putInt("mode",i);
                editor.apply();
            }
        });

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "由于系统限制(其实是我懒)，请选择文件夹内任意一张图片", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 0);
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int startnum = Integer.valueOf(start.getText().toString());
                final int endnum = Integer.valueOf(end.getText().toString());
                File file = new File(locateTv.getText().toString());
                //Log.d("EditText", locateTv.getText().toString());
                if (!file.exists()) {
                    Toast.makeText(MainActivity.this, "路径不存在", Toast.LENGTH_LONG).show();
                    return;
                }
                editor.putString("locate", String.valueOf(locateTv.getText()));
                editor.apply();
                final File[] files = file.listFiles();
                final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setCancelable(false);
                pd.setCanceledOnTouchOutside(false);
                pd.setMax(files.length);
                pd.setTitle("进度");
                pd.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        int i = 0;

                        if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton2) {
                            try {

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        String format = "yyyyMMddHHmm";
                        long targetTimeLongType;

                        if (!editFormat.getText().toString().equals("")) {
                            format = editFormat.getText().toString();
                        }

                        for (File f : files) {
                            Log.d("File:", f.getName());
                            i++;

                            if (i >= startnum && (endnum == 0 || i <= endnum)) {
                                String time = f.getName();
                                String regEx = "[^0-9]";
                                Pattern pa = Pattern.compile(regEx);
                                Matcher m = pa.matcher(time);
                                time = m.replaceAll("").trim();
                                if (time.contains("20") && time.substring(time.indexOf("20")).length() >= 12) {
                                    if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton) {
                                        try {
                                            String targetTime = time.substring(time.indexOf("20"), time.indexOf("20") + 12);
                                            if (format.equals("yyyyMMddHHmm")) {
                                                targetTimeLongType = new SimpleDateFormat(format, Locale.getDefault()).parse(targetTime).getTime();
                                            } else {
                                                targetTimeLongType = new SimpleDateFormat(format, Locale.getDefault()).parse(f.getName()).getTime();
                                            }
                                            if (f.setLastModified(targetTimeLongType)) {
                                                Log.d("succese", new Date(f.lastModified()).toString());
                                            } else {
                                                Log.d("fail", String.valueOf(targetTimeLongType));
                                                support = false;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        try {

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    /*try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }*/
                                }

                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.incrementProgressBy(1);
                                    }
                                });
                            } else if (i>endnum) break;

                        }

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                if (!support) {
                                    Toast.makeText(MainActivity.this, "您的系统极有可能不支持此操作，请更换操作模式", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        Looper.prepare();
                        Toast.makeText(MainActivity.this, "完成！", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                }).start();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showAbout();
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void showAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.about,null);
        WebView webview = (WebView) view.findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("file:///android_asset/about.html");
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.addJavascriptInterface(this,"openGit");
        builder.setView(view);
        builder.setPositiveButton("确定", null);
        builder.show();
    }

    @JavascriptInterface
    public void openGit () {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/singleNeuron/PhotoTimeFixforAndroid"));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 0) {
                Uri originalUri = data.getData();
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                if (originalUri != null) {
                    cursor = getContentResolver().query(originalUri, proj, null, null, null);
                }
                if (cursor != null) {
                    int column_index;
                    column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(column_index);
                    path = path.substring(0, path.lastIndexOf("/"));
                    locateTv.setText(path);
                    cursor.close();
                    editor.putString("locate", path);
                    editor.apply();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "选择出错，请手动填写路径并联系开发者", Toast.LENGTH_LONG).show();
        }
    }

}