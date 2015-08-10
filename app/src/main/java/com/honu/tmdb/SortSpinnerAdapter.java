package com.honu.tmdb;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Custom adapter for sort spinner:
 *
 * - display icon-only for selected sort method
 * - display icon + text for items in drop-down list
 */
class SortSpinnerAdapter extends ArrayAdapter<SortOption> implements SpinnerAdapter {

    private MoviePosterGridFragment moviePosterGridFragment;

    public SortSpinnerAdapter(MoviePosterGridFragment moviePosterGridFragment, Context context, List<SortOption> options) {
        super(context, android.R.layout.simple_spinner_item, options);
        this.moviePosterGridFragment = moviePosterGridFragment;
        //super(context, android.R.layout.simple_spinner_dropdown_item);
        //addAll(SortOption.getSortOptions());
    }

    // Displays icon-only for toolbar Spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SortOption option = getItem(position);
        if (convertView == null) {
            convertView = moviePosterGridFragment.getLayoutInflater(null).inflate(R.layout.sort_spinner_item, null);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        imageView.setImageDrawable(ResourcesCompat.getDrawable(moviePosterGridFragment.getResources(), option.itemDrawable, null));

        return convertView;
    }

    // Displays icon and text drop-down
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        SortOption option = getItem(position);
        if (convertView == null) {
            convertView = moviePosterGridFragment.getLayoutInflater(null).inflate(R.layout.sort_spinner_dropdown_item, null);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        TextView textView = (TextView) convertView.findViewById(R.id.text);
        imageView.setImageDrawable(ResourcesCompat.getDrawable(moviePosterGridFragment.getResources(), option.dropdownDrawable, null));
        textView.setText(option.text);

        return convertView;
    }
}
