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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private static final float MAX_IMAGE_SIZE = 115;
    private String myUrl;
    private Context myContext;
    private int myWidth = 0, myHeight = 0;

    public ImageDownloaderTask(ImageView imageView, Context context) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        myContext = context;
        myWidth = imageView.getWidth();
        myHeight = imageView.getHeight();
    }

    public ImageDownloaderTask(ImageView imageView, Context context, int width, int height) {
        imageViewReference = new WeakReference<ImageView>(imageView);
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
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
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