package org.mcal.mcpe_dumper;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.view.*;
import org.mcal.mcpe_dumper.nativeapi.*;
import org.mcal.mcpe_dumper.util.*;

public class MenuActivity extends AppCompatActivity
{
	private String path;

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_activity);

		path = getIntent().getExtras().getString("filePath");
	}

	public void toNameDemangler(View view)
	{
		startActivity(new Intent(this, NameDemanglerActivity.class));
	}

	private void _saveSymbols()
	{
		String [] strings=new String[Dumper.symbols.size()];
		for (int i=0;i < Dumper.symbols.size();++i)
			strings[i] = Dumper.symbols.get(i).getName();

		FileSaver saver=new FileSaver(this, Environment.getExternalStorageDirectory().toString() + "/MCPEDumper/symbols/", "Symbols.txt", strings);
		saver.save();

		String [] strings_=new String[Dumper.symbols.size()];
		for (int i=0;i < Dumper.symbols.size();++i)
			strings_[i] = Dumper.symbols.get(i).getDemangledName();
		FileSaver saver_=new FileSaver(this, Environment.getExternalStorageDirectory().toString() + "/MCPEDumper/symbols/", "Symbols_demangled.txt", strings_);
		saver_.save();


	}

	private AlertDialog mDialog;
	private Snackbar mBar;
	private Handler mHandler=new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{

			super.handleMessage(msg);
			if (mDialog != null)
				mDialog.dismiss();
			mDialog = null;
			if (mBar != null)
				mBar.show();
			else
				Snackbar.make(getWindow().getDecorView(), getString(R.string.done), 2500).show();
		}
	};
	public void saveSymbols(View view)
	{
		mDialog = new AlertDialog.Builder(this).setTitle(R.string.saving).create();
		mDialog.show();
		mBar = Snackbar.make(getWindow().getDecorView(), getString(R.string.done), 2500);
		new Thread()
		{
			public void run()
			{
				_saveSymbols();
				Message msg=new Message();
				mHandler.sendMessage(msg);
			}
		}.start();
	}
}
