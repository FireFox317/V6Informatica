package com.kruiper.timon.v6informatica.helper;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.kruiper.timon.v6informatica.R;
import com.kruiper.timon.v6informatica.objects.Auto;

import java.util.ArrayList;

/**
 * Created by timon on 15-3-2016.
 */
public class AutosList extends ArrayAdapter<Auto> implements Filterable {

	private ArrayList<Auto> originalList;
	private ArrayList<Auto> AutoList;
	private AutoFilter filter;

	public AutosList(Activity context, ArrayList<Auto> lAuto) {
		super(context, 0, lAuto);
		this.AutoList = new ArrayList<Auto>();
		this.AutoList.addAll(lAuto);
		this.originalList = new ArrayList<Auto>();
		this.originalList.addAll(lAuto);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Auto auto = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.autoslistlayout, parent, false);
		}

		TextView lvMerk = (TextView) convertView.findViewById(R.id.lvMerk);
		TextView lvType = (TextView) convertView.findViewById(R.id.lvType);
		TextView lvTypeBrandstof = (TextView) convertView.findViewById(R.id.lvTypeBrandstof);
		TextView lvKenteken = (TextView) convertView.findViewById(R.id.lvKenteken);
		ImageView imgview = (ImageView) convertView.findViewById(R.id.imageView);

		lvMerk.setText(auto.merk);
		lvType.setText(auto.type);
		lvTypeBrandstof.setText(auto.type_brandstof);
		lvKenteken.setText(auto.kenteken);

		if(auto.merk.trim().toLowerCase().contains("bmw")){
			imgview.setImageBitmap(decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.bmw, 100, 100));
		} else if(auto.merk.trim().toLowerCase().contains("mercedes")){
			imgview.setImageBitmap(decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.mercedes, 100, 100));
		} else if(auto.merk.trim().toLowerCase().contains("toyota")){
			imgview.setImageBitmap(decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.toyota, 100, 100));
		} else if(auto.merk.trim().toLowerCase().contains("volkswagen")){
			imgview.setImageBitmap(decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.volkswagen, 100, 100));
		} else if(auto.merk.trim().toLowerCase().contains("volvo")){
			imgview.setImageBitmap(decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.volvo, 100, 100));
		} else if(auto.merk.trim().toLowerCase().contains("ferrari")){
			imgview.setImageBitmap(decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.ferrari, 100, 100));
		} else if(auto.merk.trim().toLowerCase().contains("audi")){
			imgview.setImageBitmap(decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.audi, 100, 100));
		} else if(auto.merk.trim().toLowerCase().contains("porsche")){
			imgview.setImageBitmap(decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.porsche, 100, 100));
		}else {

		}
		return convertView;
	}

	@Override
	public Filter getFilter() {
		if (filter == null){
			filter  = new AutoFilter();
		}
		return filter;
	}

	private class AutoFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if(constraint != null && constraint.toString().length() > 0)
			{
				ArrayList<Auto> filteredItems = new ArrayList<Auto>();

				for(int i = 0, l = originalList.size(); i < l; i++)
				{
					Auto auto = originalList.get(i);
					if(auto.toString().toLowerCase().contains(constraint))
						filteredItems.add(auto);
				}
				result.count = filteredItems.size();
				result.values = filteredItems;
			}
			else
			{
				synchronized(this)
				{
					result.values = originalList;
					result.count = originalList.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
		                              FilterResults results) {

			AutoList = (ArrayList<Auto>)results.values;
			notifyDataSetChanged();
			clear();
			for(int i = 0, l = AutoList.size(); i < l; i++)
				add(AutoList.get(i));
			notifyDataSetInvalidated();
		}
	}

	@Override
	public long getItemId(int position) {
		Auto auto = getItem(position);
		return auto.autoid;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	                                                     int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}


}
