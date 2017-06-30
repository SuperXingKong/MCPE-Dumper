package org.mcal.mcpe_dumper;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import org.mcal.mcpe_dumper.nativeapi.*;
import org.mcal.mcpe_dumper.vtable.*;

public class SymbolActivity extends AppCompatActivity
{
	private String path;
	private String name;
	private int type;
	private String demangledName;
	private String className;
	private long size;
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


		setContentView(R.layout.symbol_activity);
		type = getIntent().getExtras().getInt("type");
		size = getIntent().getExtras().getLong("size");
		name = getIntent().getExtras().getString("name");
		demangledName = getIntent().getExtras().getString("demangledName");
		path = getIntent().getExtras().getString("filePath");

		ImageView imageTitile=(ImageView)findViewById(R.id.symbolactivityImageView);
		imageTitile.setImageResource(R.drawable.ic_package);

		TextView textName=(TextView)findViewById(R.id.symbolactivityTextViewName);
		textName.setText(name);

		TextView textDemangledName=(TextView)findViewById(R.id.symbolactivityTextViewDemangledName);
		textDemangledName.setText(demangledName);

		TextView sizeName=(TextView)findViewById(R.id.symbolactivityTextViewSize);
		sizeName.setText(new Integer((int)size).toString());
		
		
		String arguments=new String();
		if (demangledName.indexOf("(") != -1 && demangledName.lastIndexOf(")") != -1)
			arguments = demangledName.substring(demangledName.indexOf("(") + 1, demangledName.lastIndexOf(")"));
		else
			arguments = "NULL";
		TextView textArguments=(TextView)findViewById(R.id.symbolactivityTextViewArguments);
		textArguments.setText(arguments);

		String symbolMainName=new String();
		if (demangledName.indexOf("(") != -1)
			symbolMainName = demangledName.substring(0, demangledName.indexOf("("));
		else
			symbolMainName = demangledName;
		className=new String();
		if (symbolMainName.lastIndexOf("::") != -1)
			className = symbolMainName.substring(0, symbolMainName.lastIndexOf("::"));
		else if (symbolMainName.startsWith("vtable"))
			className = symbolMainName.substring(symbolMainName.lastIndexOf(" ") + 1, symbolMainName.length());
		else
			className = "NULL";
		TextView textClassName=(TextView)findViewById(R.id.symbolactivityTextClass);
		textClassName.setText(className);

		String symbolName=new String();
		if (symbolMainName.lastIndexOf("::") != -1)
			symbolName = symbolMainName.substring(symbolMainName.lastIndexOf("::") + 2, symbolMainName.length());
		else
			symbolName = symbolMainName;

		TextView textSymbolName=(TextView)findViewById(R.id.symbolactivityTextViewSymbolMainName);
		textSymbolName.setText(symbolName);

		String typeName=Tables.symbol_type.get(type);

		TextView textTypeName=(TextView)findViewById(R.id.symbolactivityTextViewType);
		textTypeName.setText(typeName);

		if (name.startsWith("_ZTV"))
		{
			findViewById(R.id.symbolactivityButtonFloat).setVisibility(View.VISIBLE);
			findViewById(R.id.symbolactivityTextViewButtonFloat).setVisibility(View.VISIBLE);
		}

		if (className != "NULL")
		{
			findViewById(R.id.symbolactivityButtonFloatClass).setVisibility(View.VISIBLE);
			findViewById(R.id.symbolactivityTextViewButtonFloatClass).setVisibility(View.VISIBLE);
		}
	}

	public void toVtableActivity(View view)
	{
		showProgressDialog();
		new Thread()
		{
			public void run()
			{
				MCPEVtable vtable=VtableDumper.dump(SymbolActivity.this.path, SymbolActivity.this.name);
				if (vtable != null)
					SymbolActivity.this.toVtableActivity_(vtable);
				dismissProgressDialog();
			}
		}.start();
	}
	
	public void toClassActivity(View view)
	{
		showProgressDialog();
		new Thread()
		{
			public void run()
			{
				MCPEVtable vtable=VtableDumper.dump(SymbolActivity.this.path, SymbolActivity.this.name);
				if (vtable != null)
					SymbolActivity.this.toClassActivity_(vtable);
				dismissProgressDialog();
			}
		}.start();
	}

	public void toClassActivity_(MCPEVtable vtable)
	{
		if(className==null||className==""||className==" "||className.isEmpty()||className=="NULL")
			return;
		Bundle bundle=new Bundle();
		bundle.putString("name", className);
		bundle.putString("path", path);
		Intent intent=new Intent(this, ClassActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void toVtableActivity_(MCPEVtable vtable)
	{
		Bundle bundle=new Bundle();
		bundle.putString("name", name);
		bundle.putString("path", path);
		Dumper.exploed.add(vtable);
		Intent intent=new Intent(this, VtableActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private AlertDialog dialog;

	public void showProgressDialog()
	{
		dialog = new AlertDialog.Builder(this).setTitle(R.string.loading).create();
		dialog.show();
	}
	
	public void dismissProgressDialog()
	{
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
	}
}
