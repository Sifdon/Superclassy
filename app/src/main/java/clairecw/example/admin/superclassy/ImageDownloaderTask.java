package clairecw.example.admin.superclassy;

/**
 * Created by admin on 4/20/16.
 */
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpStatus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private final WeakReference<TextView> textViewReference;
    private static final float MAX_IMAGE_SIZE = 115;
    private String myUrl;
    private Context myContext;
    private int myWidth = 0, myHeight = 0;

    public ImageDownloaderTask(ImageView imageView, Context context) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        textViewReference = null;
        myContext = context;
        myWidth = imageView.getWidth();
        myHeight = imageView.getHeight();
    }

    public ImageDownloaderTask(TextView textView, Context context) {
        textViewReference = new WeakReference<TextView>(textView);
        imageViewReference = null;
        myContext = context;
        myWidth = textView.getWidth();
        myHeight = textView.getHeight();
    }

    public ImageDownloaderTask(ImageView imageView, Context context, int width, int height) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        textViewReference = null;
        myContext = context;
        myWidth = width;
        myHeight = height;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    //imageView.setImageBitmap(scaleDown(bitmap, MAX_IMAGE_SIZE, true));

                    if (myWidth == 0) {
                        myWidth = imageView.getWidth();
                        myHeight = imageView.getHeight();
                    }
                    Picasso.with(myContext)
                            .load(myUrl)
                            .resize(myWidth, myHeight)
                            .centerCrop()
                            .into(imageView);
                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.img);
                    imageView.setImageDrawable(placeholder);
                }
            }

        }
        else if (textViewReference != null) {
            final TextView textView = textViewReference.get();
            if (textView != null) {
                if (bitmap != null) {
                    //imageView.setImageBitmap(scaleDown(bitmap, MAX_IMAGE_SIZE, true));

                    if (myWidth == 0) {
                        myWidth = textView.getWidth();
                        myHeight = textView.getHeight();
                    }

                    Picasso.with(myContext)
                            .load(myUrl)
                            .resize(myWidth, myHeight)
                            .centerCrop()
                            .into(new Target() {

                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    textView.setBackground(new BitmapDrawable(myContext.getResources(), toGrayscale(bitmap)));
                                }

                                @Override
                                public void onBitmapFailed(final Drawable errorDrawable) {
                                    Log.d("TAG", "FAILED");
                                }

                                @Override
                                public void onPrepareLoad(final Drawable placeHolderDrawable) {
                                    Log.d("TAG", "Prepare Load");
                        }
                    });
                } else {
                    Drawable placeholder = textView.getContext().getResources().getDrawable(R.drawable.img);
                    textView.setBackground(placeholder);
                }
            }
        }
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        myUrl = url;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            if (urlConnection != null) urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}