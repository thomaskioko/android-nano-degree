package com.thomaskioko.moviemaniac.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thomaskioko.moviemaniac.data.FavoriteMovieDbHelper;
import com.thomaskioko.moviemaniac.data.FavoritesContract;

/**
 * @author Thomas Kioko
 */
public class FavoriteMovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoriteMovieDbHelper mFavoriteMovieDbHelper;
    public static final int FAVORITE = 300;
    public static final int FAVORITE_WITH_ID = 101;


    @Override
    public boolean onCreate() {
        mFavoriteMovieDbHelper = new FavoriteMovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "favorite/"
            case FAVORITE: {
                retCursor = getFavoriteData(projection, selection, selectionArgs, sortOrder);
                break;
            }
            // "favorite/*"
            case FAVORITE_WITH_ID: {
                retCursor = getFavoriteMovie(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (retCursor != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITE:
                return FavoritesContract.FavoriteMovieEntry.CONTENT_TYPE;
            case FAVORITE_WITH_ID:
                return FavoritesContract.FavoriteMovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mFavoriteMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITE: {
                long _id = db.insert(FavoritesContract.FavoriteMovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = FavoritesContract.FavoriteMovieEntry.buildFavoritesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Register a content observer to watch for changes on the uri and notify the UI when th cursor changes
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mFavoriteMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case FAVORITE: {
                rowsDeleted = db.delete(FavoritesContract.FavoriteMovieEntry.TABLE_NAME, selection, selectionArgs);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            //Register a content observer to watch for changes on the uri and notify the UI when th cursor changes
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mFavoriteMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {

            case FAVORITE: {
                rowsUpdated = db.update(FavoritesContract.FavoriteMovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Register a content observer to watch for changes on the uri and notify the UI when th cursor changes
        if (rowsUpdated != 0) {
            //Register a content observer to watch for changes on the uri and notify the UI when th cursor changes
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mFavoriteMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FavoritesContract.FavoriteMovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    /*
    Students: Here is where you need to create the UriMatcher. This UriMatcher will
    match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
    and FAVORITE integer constants defined above.  You can test this by uncommenting the
    testUriMatcher test within TestUriMatcher.
 */
    public static UriMatcher buildUriMatcher() {

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoritesContract.CONTENT_AUTHORITY;

        //Create a corresponding code for each URI
        uriMatcher.addURI(authority, FavoritesContract.PATH_FAVORITE, FAVORITE);
        uriMatcher.addURI(authority, FavoritesContract.PATH_FAVORITE + "/*", FAVORITE_WITH_ID);
        return uriMatcher;
    }

    /**
     * This function fetches data based on the query params passed.
     *
     * @param projection    Projection parameters
     * @param selection     Select parameters
     * @param selectionArgs Selection Arguments
     * @param sortOrder     Sort Order
     * @return {@link Cursor}
     */
    private Cursor getFavoriteData(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mFavoriteMovieDbHelper.getReadableDatabase().query(
                FavoritesContract.FavoriteMovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Get Favorite Movie using the ID
     *
     * @param uri        {@link Uri}
     * @param projection parameters
     * @param sortOrder  Sort Order
     * @return {@link Cursor}
     */
    private Cursor getFavoriteMovie(Uri uri, String[] projection, String sortOrder) {
        return mFavoriteMovieDbHelper.getReadableDatabase().query(
                FavoritesContract.FavoriteMovieEntry.TABLE_NAME,
                projection,
                FavoritesContract.FavoriteMovieEntry._ID + " = ?",
                new String[]{String.valueOf(ContentUris.parseId(uri))},
                null,
                null,
                sortOrder);
    }

}
