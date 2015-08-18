package com.honu.tmdb;

import java.util.ArrayList;
import java.util.List;

/**
 * Options for the sort spinner
 */
class SortOption {
    private int id;
    private int itemDrawable;
    private int dropdownDrawable;
    private String text;

    public static final int POPULARITY = 0;
    public static final int RATING = 1;
    public static final int FAVORITE = 2;


    static List<SortOption> options = new ArrayList<>();

    public SortOption(int id, String text, int itemDrawable, int dropdownDrawable) {
        this.id = id;
        this.text = text;
        this.itemDrawable = itemDrawable;
        this.dropdownDrawable = dropdownDrawable;
    }

    public static List<SortOption> getSortOptions() {
        if (options.isEmpty()) {
            options.add(new SortOption(POPULARITY, "most popular", R.drawable.ic_trending_up_white_24dp, R.drawable.ic_trending_up_black_24dp));
            options.add(new SortOption(RATING, "highest rated", R.drawable.ic_star_white_24dp, R.drawable.ic_star_border_black_24dp));
            options.add(new SortOption(FAVORITE, "favorites", R.drawable.ic_favorite_white_24dp, R.drawable.ic_favorite_border_black_24dp));
        }
        return options;
    }

    public static int getSortMethod(int position) {
        return options.get(position).id;
    }

    public int getId() {
        return id;
    }

    public int getItemDrawable() {
        return itemDrawable;
    }

    public int getDropdownDrawable() {
        return dropdownDrawable;
    }

    public String getText() {
        return text;
    }
}
