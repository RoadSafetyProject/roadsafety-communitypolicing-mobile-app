package wappi.iRoadCommunityPolicing.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wappi.iRoadCommunityPolicing.R;


public class DrawerListCustomAdapter extends BaseAdapter
{
    public String title[];
	public int images[]={R.drawable.ic_favourites,R.drawable.ic_share,
            R.drawable.ic_drawer_settings,R.drawable.ic_drawer_feedback};

	public Activity context;
	public LayoutInflater inflater;
    public static int selection;

	public DrawerListCustomAdapter(Activity context, String[] title) {
		super();

		this.context = context;
		this.title = title;
	    this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selection=0;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return title.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static class ViewHolder
	{
		ImageView imgViewLogo;
		TextView txtViewTitle;
		RelativeLayout divider;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
        ViewHolder holder;
        holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.draweritem_row, null);
        holder.txtViewTitle = (TextView) convertView.findViewById(R.id.drawer_row);
        convertView.setTag(holder);
        holder.txtViewTitle.setText(title[position]);
        boolean selected=(selection==position)?true:false;
        formatNavDrawerItem(selected,holder);
		return convertView;
	}
    private void formatNavDrawerItem(boolean selected,ViewHolder holder) {
        // configure its appearance according to whether or not it's selected
        holder.txtViewTitle.setTextColor(selected ?
                context.getResources().getColor(R.color.dark_dvs) :
                context.getResources().getColor(R.color.navdrawer_text_color));
        if(false){
            holder.imgViewLogo.setColorFilter(selected ?
                    context.getResources().getColor(R.color.purple_500) :
                    context.getResources().getColor(R.color.transparent));
        }
    }

}