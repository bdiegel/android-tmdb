package com.honu.tmdb.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.honu.tmdb.rest.Genre;
import com.honu.tmdb.rest.Movie;


@Database(
      entities = {Movie.class, Genre.class},
      version = 2
)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase sInstance;

    public abstract MovieDao movieDao();

    public static final MovieDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MovieDatabase.class) {
                if (sInstance == null) {

                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                          MovieDatabase.class, "movies.db")
                          .addMigrations(MIGRATION_1_2)
//                          .allowMainThreadQueries() // @TODO: remove me
                          .build();
                }
            }
        }

        return  sInstance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
}
