package org.mcal.mcpe_dumper;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import org.mcal.mcpe_dumper.nativeapi.*;
import org.mcal.mcpe_dumper.util.*;
import android.content.pm.PackageManager.*;
import android.content.pm.*;
import java.io.*;

public class MainActivity extends AppCompatActivity
{
	private String path;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		TextView textViewSavePath=(TextView)findViewById(R.id.mainactivityTextViewSavePath);
		textViewSavePath.setText(getString(R.string.savedIn) + Environment.getExternalStorageDirectory().toString() + "/MCPEDumper/*");
	}

	public void chooseSystem(View view)
	{
		Context mcPackageContext = null;

		try
		{
			mcPackageContext = createPackageContext("com.mojang.minecraftpe", CONTEXT_IGNORE_SECURITY);
		}
		catch (PackageManager.NameNotFoundException e)
		{
			AlertDialog dialog=new AlertDialog.Builder(this).setTitle(R.string.error).setMessage(R.string.noMCPE).create();
			dialog.show();
		}

		loadSo(mcPackageContext.getApplicationInfo().nativeLibraryDir + File.separator + "libminecraftpe.so");
	}

	private void loadSo(final String path)
	{
		showProgressDialog();
		this.path = path;
		new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				
				MCPEDumper.load(path);
				Dumper.readData(path);
				toClassesActivity();
			}
		}.start();
	}

	private AlertDialog dialog;

	public void showProgressDialog()
	{
		dialog = new AlertDialog.Builder(MainActivity.this).setTitle(R.string.loading).create();
		dialog.show();
	}
	public void dismissProgressDialog()
	{
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
	}
	public void toClassesActivity()
	{
		Bundle bundle=new Bundle();
		bundle.putString("filePath", path);
		Intent intent=new Intent(MainActivity.this, SymbolsActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		dismissProgressDialog();
	}

    static
	{
        System.loadLibrary("mcpedumper");
    }
}
