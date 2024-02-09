package org.coolreader;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import org.coolreader.crengine.BookInfo;
import org.coolreader.crengine.Bookmark;
import org.coolreader.crengine.CoverpageManager;
import org.coolreader.crengine.FileInfo;
import org.coolreader.crengine.L;
import org.coolreader.crengine.Logger;
import org.coolreader.crengine.Utils;
import org.coolreader.db.CoverDB;
import org.coolreader.db.MainDB;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookContentProvider extends ContentProvider
{
	public static final Logger log = L.create("provider");

	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	final String[] sProjection = new String[] {MediaStore.MediaColumns.TITLE, MediaStore.MediaColumns.AUTHOR, MediaStore.MediaColumns.DURATION, MediaStore.MediaColumns.XMP, MediaStore.MediaColumns.DATA};
	private File mDBDir;
	private MainDB mMainDB;
	private CoverDB mCoverDB = new CoverDB();
	private CoverpageManager mCoverpageManager;

	static
	{
		sUriMatcher.addURI(BuildConfig.APPLICATION_ID, "reading", 1);
		sUriMatcher.addURI(BuildConfig.APPLICATION_ID, "reading/#/#/#", 1);
	}

	private File getDatabaseDir()
	{
		File cr3dir = new File(Environment.getExternalStorageDirectory(), ".cr3");
		if (!cr3dir.isDirectory() || !cr3dir.canWrite())
		{
			log.w("Cannot use " + cr3dir + " for writing database, will use data directory instead");
			cr3dir = getContext().getFilesDir();
			log.w("getFilesDir=" + cr3dir + " getDataDirectory=" + Environment.getDataDirectory());
		}
		log.i("DB directory: " + cr3dir);
		return cr3dir;
	}

	@Override
	public boolean onCreate()
	{
		mDBDir = getDatabaseDir();
		if (mDBDir == null)
			return false;

		mMainDB = new MainDB();
		mCoverDB = new CoverDB();

		mCoverpageManager = new CoverpageManager();
		return true;
	}

	private int getInteger(Uri uri, int position, int def)
	{
		final List<String> pathSegments = uri.getPathSegments();
		if (pathSegments.size() > position)
		{
			try
			{
				return Integer.parseInt(pathSegments.get(position));
			} catch (Exception E)
			{
				return def;
			}
		} else
			return def;
	}

	@Override
	public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1)
	{
		final int type = sUriMatcher.match(uri);
		if (type == 1)
		{
			mMainDB.open(mDBDir);
			if (!mMainDB.isOpened())
				return null;
			mCoverDB.open(mDBDir);

			final ArrayList<BookInfo> list = mMainDB.loadRecentBooks(getInteger(uri, 1, 2));
			mCoverpageManager.setCoverpageSize(getInteger(uri, 2, 110), getInteger(uri, 3, 140));

			final MatrixCursor cursor = new MatrixCursor(sProjection, list.size());
			for (BookInfo bi : list)
			{
				final FileInfo fi = bi.getFileInfo();

				final Object[] v = new Object[sProjection.length];
				v[0] = fi.getTitleOrFileName();
				v[1] = fi.getAuthors();

				final Bookmark bm = bi.getLastPosition();
				if (bm != null)
					v[2] = Utils.formatLastPosition(getContext(), bm);
				v[3] = mCoverDB.loadBookCoverpage(fi.getPathName());
				v[4] = fi.getPathName();

				cursor.addRow(v);
			}

			mMainDB.close();
			mCoverDB.close();

			return cursor;
		}

		return null;
	}

	@Override
	public String getType(Uri uri)
	{
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues)
	{
		return null;
	}

	@Override
	public int delete(Uri uri, String s, String[] strings)
	{
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String s, String[] strings)
	{
		return 0;
	}
}
