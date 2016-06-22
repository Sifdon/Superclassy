package clairecw.example.admin.superclassy;

/**
 * Created by admin on 6/21/16.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PostAdapter extends BaseAdapter {
    ArrayList<String> result, urls, descriptions, authors;
    Context context;
    private static LayoutInflater inflater=null;
    public PostAdapter(Activity mainActivity, ArrayList<String> prgmNameList, ArrayList<String> prgmImages, ArrayList<String> dscs, ArrayList<String> auth) {
        // TODO Auto-generated constructor stub
        result = prgmNameList;
        context = mainActivity;
        urls = prgmImages;
        descriptions = dscs;
        authors = auth;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv, desc, author;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.post_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView);
        holder.tv.setText(result.get(position));
        holder.desc = (TextView)rowView.findViewById(R.id.textView2);
        holder.desc.setText(descriptions.get(position));
        holder.author = (TextView)rowView.findViewById(R.id.author);
        holder.author.setText("By: " + authors.get(position));
        new ImageDownloaderTask(holder.img, context, 100, 100).execute(urls.get(position));
        return rowView;
    }

}
