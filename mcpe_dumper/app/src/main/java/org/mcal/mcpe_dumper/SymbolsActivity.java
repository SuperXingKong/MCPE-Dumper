package org.mcal.mcpe_dumper;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.util.*;
import org.mcal.mcpe_dumper.*;
import org.mcal.mcpe_dumper.nativeapi.*;

public class SymbolsActivity extends AppCompatActivity
{
	private ListView list; 
    private List<Map<String, Object>> data; 
	private String path;
    @Override 
    public void onCreate(Bundle savedInstanceState)
	{ 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.symbols_activity);
		
        list = (ListView)findViewById(R.id.symbols_activity_list_view); 
		data = getData();
		SymbolsAdapter adapter = new SymbolsAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ItemClickListener());
		
		path=getIntent().getExtras().getString("filePath");
    }

    private List<Map<String, Object>> getData() 
    { 
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); 
        Map<String, Object> map;
        for (int i=0;i < Dumper.symbols.size();++i)
        { 
            map = new HashMap<String, Object>(); 
			
			if (Dumper.symbols.get(i).getType() == 1)
				map.put("img", R.drawable.ic_lumx); 
			else if (Dumper.symbols.get(i).getType() == 2)
				map.put("img", R.drawable.ic_package);
			else map.put("img", R.drawable.ic_note);
			map.put("title", Dumper.symbols.get(i).getDemangledName());
            map.put("info", Dumper.symbols.get(i).getName());
			map.put("type", Dumper.symbols.get(i).getType());
            map.put("size", Dumper.symbols.get(i).getSize());
            
			list.add(map);
        } 
        return list; 
    } 
	
	public void showSearch(View view)
	{
		Intent i=new Intent(this,SearchActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("filePath",path);
		i.putExtras(bundle);
		startActivity(i);
	}
	
	public void showMenu(View view)
	{
		Intent i=new Intent(this,MenuActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("filePath",path);
		i.putExtras(bundle);
		startActivity(i);
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
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,long arg3)
		{
			Bundle bundle=new Bundle();
			bundle.putString("demangledName",(String)(((ViewHolder)view.getTag()).title.getText()));
			bundle.putString("name",(String)(((ViewHolder)view.getTag()).info.getText()));
			bundle.putInt("type",((ViewHolder)view.getTag()).type);
			bundle.putString("filePath",path);
			bundle.putLong("size",((ViewHolder)view.getTag()).size);
			
			Intent intent=new Intent(SymbolsActivity.this,SymbolActivity.class);
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
			holder.type=((int)data.get(position).get("type"));

			return convertView; 
		}
    }
}
