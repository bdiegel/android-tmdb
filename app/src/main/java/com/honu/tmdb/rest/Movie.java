package com.honu.tmdb.rest;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.honu.tmdb.MovieGenre;

import java.util.ArrayList;
import java.util.List;

//import com.honu.tmdb.data.MovieContract;

/**
 *
 */
@Entity(tableName = "movie")
public class Movie implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    @SerializedName("id")
    int id;

    @ColumnInfo(name = "adult")
    @SerializedName("adult")
    boolean isAdult;

    @ColumnInfo(name = "backdrop_path")
    @SerializedName("backdrop_path")
    String backdropPath;

    @Ignore
    @SerializedName("genre_ids")
    int[] genreIds = new int[0];

    @SerializedName("original_language")
    String originalLanguage;

    @ColumnInfo(name = "original_title")
    @SerializedName("original_title")
    String originalTitle;

    @ColumnInfo(name = "overview")
    @SerializedName("overview")
    String overview;

    @ColumnInfo(name = "release")
    @SerializedName("release_date")
    String releaseDate;

    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    String posterPath;

    @ColumnInfo(name = "popularity")
    @SerializedName("popularity")
    float popularity;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    String title;

    @ColumnInfo(name = "video")
    @SerializedName("video")
    boolean isVideo;

    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    float voteAverage;

    @ColumnInfo(name = "vote_count")
    @SerializedName("vote_count")
    int voteCount;

    static final String BASE_IMG_URL = "http://image.tmdb.org/t/p/";

    // Poster image sizes
    static final String POSTER_SIZE_W92 = "w92";
    static final String POSTER_SIZE_W154 = "w154";
    static final String POSTER_SIZE_W185 = "w185";
    static final String POSTER_SIZE_W342 = "w342";
    static final String POSTER_SIZE_W500 = "w500";
    static final String POSTER_W780 = "w780";
    static final String POSTER_SIZE_ORIGINAL = "original";

    // Backdrop image sizes
    static final String BACKDROP_SIZE_W300 = "w300";
    static final String BACKDROP_SIZE_W780 = "w780";
    static final String BACKDROP_SIZE_W1280 = "w1280";
    static final String BACKDROP_SIZE_ORIGINAL = "original";

    // recommended for most phones:
    static final String SIZE_DEFAULT = POSTER_SIZE_W185;

    @Ignore
    public Movie() {
    }

    public Movie(int id, boolean isAdult, String backdropPath, String originalLanguage, String originalTitle,
                 String overview, String releaseDate, String posterPath, float popularity, String title,
                 boolean isVideo, float voteAverage, int voteCount) {
        this.id = id;
        this.isAdult = isAdult;
        this.backdropPath = backdropPath;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.popularity = popularity;
        this.title = title;
        this.isVideo = isVideo;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
    }

    public int getId() {
        return id;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getBackdropUrl() {
        return BASE_IMG_URL + BACKDROP_SIZE_W300 + backdropPath;
    }

    public String getBackdropUrl(int screenWidth) {
        if (screenWidth >= 1024) {
            return BASE_IMG_URL + BACKDROP_SIZE_W780 + backdropPath;
        }
        return BASE_IMG_URL + BACKDROP_SIZE_W300 + backdropPath;
    }

    public int[] getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(int[] genreIds) {
        this.genreIds = genreIds;
    }

    public List<String> getMovieGenres() {
        List<String> genres = new ArrayList<>();
        int[] ids = getGenreIds();
        for (int i=0; i<ids.length; i++) {
            MovieGenre genre = MovieGenre.getById(ids[i]);
            if (genre != null) {
                genres.add(genre.getTitle());
            }
        }
        return genres;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getPosterUrl() {
        return BASE_IMG_URL + POSTER_SIZE_W185 + posterPath;
    }

    public String getPosterUrl(int screenWidth) {
        if (screenWidth >= 1024) {
            return BASE_IMG_URL + POSTER_SIZE_W342 + posterPath;
        }
        return BASE_IMG_URL + POSTER_SIZE_W185 + posterPath;
    }

    public float getPopularity() {
        return popularity;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeByte(isAdult ? (byte) 1 : (byte) 0);
        dest.writeString(this.backdropPath);
        dest.writeIntArray(this.genreIds);
        dest.writeString(this.originalLanguage);
        dest.writeString(this.originalTitle);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
        dest.writeString(this.posterPath);
        dest.writeFloat(this.popularity);
        dest.writeString(this.title);
        dest.writeByte(isVideo ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.voteAverage);
        dest.writeInt(this.voteCount);
    }

    protected Movie(Parcel in) {
        this.id = in.readInt();
        this.isAdult = in.readByte() != 0;
        this.backdropPath = in.readString();
        this.genreIds = in.createIntArray();
        this.originalLanguage = in.readString();
        this.originalTitle = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.posterPath = in.readString();
        this.popularity = in.readFloat();
        this.title = in.readString();
        this.isVideo = in.readByte() != 0;
        this.voteAverage = in.readFloat();
        this.voteCount = in.readInt();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
