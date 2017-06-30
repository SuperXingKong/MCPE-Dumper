package org.mcal.mcpe_dumper;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.util.*;
import org.mcal.mcpe_dumper.nativeapi.*;
import org.mcal.mcpe_dumper.util.*;
import org.mcal.mcpe_dumper.vtable.*;

public class ClassActivity extends AppCompatActivity
{
	private String path;
	private String name;
	private ListView list; 
    private List<Map<String, Object>> data; 

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.class_activity);
		name = getIntent().getExtras().getString("name");
		path = getIntent().getExtras().getString("path");

		list = (ListView)findViewById(R.id.class_activity_list_view); 
		data = getData();
		SymbolsAdapter adapter = new SymbolsAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ItemClickListener());

		setTitle(name);
		TextView title=(TextView)findViewById(R.id.classactivityTextViewName);
		title.setText(name);

		if (hasVtable())
		{
			((TextView)findViewById(R.id.classactivityTextViewButtonFloatVtable)).setVisibility(View.VISIBLE);
			((FloatingActionButton)findViewById(R.id.classactivityButtonFloat)).setVisibility(View.VISIBLE);
		}
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == android.R.id.home)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private List<Map<String, Object>> getData() 
    { 
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); 
        Map<String, Object> map;
		MCPEClass classThis=findClass();
		if (classThis == null)
			return list;
        for (int i=0;i < classThis.getSymbols().size();++i)
        { 
            map = new HashMap<String, Object>(); 
			if (classThis.getSymbols().get(i).getType() == 1)
				map.put("img", R.drawable.ic_note); 
			else if (classThis.getSymbols().get(i).getType() == 2)
				map.put("img", R.drawable.ic_package);
			else map.put("img", R.drawable.ic_question_mark_circle);
			map.put("title", classThis.getSymbols().get(i).getDemangledName());
            map.put("info", classThis.getSymbols().get(i).getName());
			map.put("type", classThis.getSymbols().get(i).getType());
			map.put("size", classThis.getSymbols().get(i).getSize());
			
			list.add(map);
        } 
        return list; 
    }

	Handler mHandler=new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case 0:
					showSavingProgressDialog();
					break;
				case 1:
					Snackbar.make(getWindow().getDecorView(), ClassActivity.this.getString(R.string.done),2500).show();
					dismissProgressDialog();
					break;
			}
		}
	};

	public void save(View view)
	{
		new Thread()
		{
			public void run()
			{
				mHandler.sendEmptyMessage(0);
				HeaderGenerator generator=new HeaderGenerator(findClass(), findVtable(), path);
				FileSaver saver=new FileSaver(ClassActivity.this, Environment.getExternalStorageDirectory().toString() + "/MCPEDumper/headers/", getSaveName(name), generator.generate());
				saver.save();
				mHandler.sendEmptyMessage(1);
			}
		}.start();
	}

	private MCPEClass findClass()
	{
		for (MCPEClass clasz:Dumper.classes)
			if (clasz.getName().equals(name))
				return clasz;
		MCPEClass clasz=ClassGetter.getClass(name);
		return clasz;
	}

	private MCPEVtable findVtable()
	{
		for (MCPEVtable clasz:Dumper.exploed)
			if (clasz.getName().equals(getZTVName(name)))
				return clasz;
		MCPEVtable vtable=VtableDumper.dump(path, getZTVName(name));
		return vtable;
	}

	private String getZTVName(String mangledName)
	{
		String ret="_ZTV";
		String[] names=mangledName.split("::");
		for (String str:names)
			ret = ret + ((new Integer(str.length()).toString() + str));
		return ret;
	}

	private String getSaveName(String mangledName)
	{
		String ret=new String();
		String[] names=mangledName.split("::");
		boolean isFirstName=true;
		for (String str:names)
		{
			if (isFirstName)
			{
				ret = ret + str;
				isFirstName = false;
			}
			else
				ret = ret + "$" + str;
		}
		return ret + ".h";
	}

	public void toVtableActivity(View view)
	{
		showLoadingProgressDialog();
		new Thread()
		{
			public void run()
			{
				MCPEVtable vtable=VtableDumper.dump(path, getZTVName(name));
				if (vtable != null)
					ClassActivity.this.toVtableActivity_(vtable);
				dismissProgressDialog();
			}
		}.start();
	}

	private boolean hasVtable()
	{
		String vtableThis=getZTVName(name);
		for (MCPESymbol symbol:Dumper.symbols)
			if (symbol.getName().equals(vtableThis))
				return true;
		return false;
	}

	public void toVtableActivity_(MCPEVtable vtable)
	{
		Bundle bundle=new Bundle();
		bundle.putString("name", getZTVName(name));
		bundle.putString("path", path);
		Dumper.exploed.add(vtable);
		Intent intent=new Intent(this, VtableActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private AlertDialog dialog;

	public void showLoadingProgressDialog()
	{
		dialog = new AlertDialog.Builder(this).setTitle(R.string.loading).setView(R.layout.loading_dialog).setCancelable(false).create();
		dialog.show();
	}

	public void showSavingProgressDialog()
	{
		dialog = new AlertDialog.Builder(this).setTitle( getString(R.string.saving)).setView(R.layout.loading_dialog).setCancelable(false).create();
		dialog.show();
	}

	public void dismissProgressDialog()
	{
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
	}

	static class ViewHolder 
    { 
		public ImageView img;
		public TextView title;
		public TextView info;
		public int type;
		public long size;
    }

	private final class ItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3)
		{
			Bundle bundle=new Bundle();
			bundle.putString("demangledName", (String)(((ViewHolder)view.getTag()).title.getText()));
			bundle.putString("name", (String)(((ViewHolder)view.getTag()).info.getText()));
			bundle.putInt("type", ((ViewHolder)view.getTag()).type);
			bundle.putLong("size", ((ViewHolder)view.getTag()).size);
			bundle.putString("filePath", path);
			Intent intent=new Intent(ClassActivity.this, SymbolActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

    public class SymbolsAdapter extends BaseAdapter 
    {     
		private LayoutInflater mInflater = null;
		private SymbolsAdapter(Context context) 
		{ 
			this.mInflater = LayoutInflater.from(context); 
		} 

		@Override 
		public int getCount()
		{ 
			return data.size(); 
		} 

		@Override 
		public Object getItem(int position)
		{ 
			return position; 
		} 

		@Override 
		public long getItemId(int position)
		{  
			return position; 
		} 

		@Override 
		public View getView(int position, View convertView, ViewGroup parent)
		{ 
			ViewHolder holder = null; 

			if (convertView == null) 
			{ 
				holder = new ViewHolder(); 
				convertView = mInflater.inflate(R.layout.symbol_list_item, null); 
				holder.img = (ImageView)convertView.findViewById(R.id.symbolslistitemimg); 
				holder.title = (TextView)convertView.findViewById(R.id.symbolslistitemTextViewtop); 
				holder.info = (TextView)convertView.findViewById(R.id.symbolslistitemTextViewbottom); 
				convertView.setTag(holder);
			}
			else 
			{ 
				holder = (ViewHolder)convertView.getTag(); 
			} 
			holder.img.setBackgroundResource((Integer)data.get(position).get("img")); 
			holder.title.setText((String)data.get(position).get("title")); 
			holder.info.setText((String)data.get(position).get("info"));
			holder.type = ((int)data.get(position).get("type"));

			return convertView; 
		}


    }
}
