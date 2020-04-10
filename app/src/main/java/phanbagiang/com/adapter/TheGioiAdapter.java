package phanbagiang.com.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import phanbagiang.com.covid19.R;
import phanbagiang.com.model.TheGioi;

public class TheGioiAdapter extends ArrayAdapter<TheGioi> {
    Activity context;
    int resource;

    public TheGioiAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
    }
    ViewHolder holder=new ViewHolder();
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TheGioi tg= this.getItem(position);
        ViewHolder holder = null;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(this.resource, null);
            holder = new ViewHolder();
            holder.txtChet= convertView.findViewById(R.id.txtTV);
            holder.txtBinhPhuc=convertView.findViewById(R.id.txtBP);
            holder.txtNhiem=convertView.findViewById(R.id.txtNhiem);
            holder.txtTenTG=convertView.findViewById(R.id.txtTenQGTT);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.txtTenTG.setText(tg.getCountry());
        holder.txtNhiem.setText(tg.getCases());
        holder.txtBinhPhuc.setText(tg.getRecovered());
        holder.txtChet.setText(tg.getDeaths());
        return convertView;
        /*
        LayoutInflater layoutInflater=this.context.getLayoutInflater();
        View customView=layoutInflater.inflate(this.resource,null);
        TheGioi tg= this.getItem(position);
        TextView txtTenTG=customView.findViewById(R.id.txtTenQGTT);
        TextView txtNhiem=customView.findViewById(R.id.txtNhiem);
        TextView txtBinhPhuc=customView.findViewById(R.id.txtBP);
        TextView txtChet=customView.findViewById(R.id.txtTV);

        txtTenTG.setText(tg.getCountry());
        txtNhiem.setText(tg.getCases());
        txtBinhPhuc.setText(tg.getRecovered());
        txtChet.setText(tg.getDeaths());
        return customView;

         */
    }
    static class ViewHolder {
        TextView txtTenTG;
        TextView txtNhiem;
        TextView txtBinhPhuc;
        TextView txtChet;
    }
}
