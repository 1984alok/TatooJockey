package utills;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ThreadPoolExecutor;

import applabindia.com.tattoojocky.R;

/**
 * Created by Alok on 30-09-2016.
 */
public class CommonUtill {


    public static void showToast(Context context, String message, int toastTimeLength) {
        Toast toast = Toast.makeText(context, message, toastTimeLength);
        toast.getView().setBackgroundResource(R.drawable.green_bag);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);

        if (v != null) {
            v.setGravity(Gravity.CENTER);
            v.setTextColor(context.getResources().getColor(R.color.orange));
        }
        toast.show();
    }

    public static void showSnakbarSucces(Context context, String message, View anchor) {
        Snackbar snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.drawable.green_bag);
        TextView v = (TextView) snackbar.getView().findViewById(android.R.id.message);

        if (v != null) {
            v.setGravity(Gravity.CENTER);
            v.setTextColor(context.getResources().getColor(R.color.orange));
        }
        snackbar.show();
    }


    public static void showSnakbarError(Context context, String message, View anchor) {
        Snackbar snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.drawable.red_bag);
        TextView v = (TextView) snackbar.getView().findViewById(android.R.id.message);

        if (v != null) {
            v.setGravity(Gravity.CENTER);
            v.setTextColor(context.getResources().getColor(R.color.orange));
        }
        snackbar.show();
    }


    public static Bitmap getBitmapFromUrl(final String urlString,final Context mContext,Bitmap imageBmp) {
        new AsyncTask<Void,Void,Bitmap>(){
            Bitmap image = null;

            @Override
            protected void onPostExecute(Bitmap imageBmp) {
                super.onPostExecute(imageBmp);
                imageBmp = image;
            }

            @Override
            protected Bitmap doInBackground(Void... params) {

                try {

                    if(!urlString.equalsIgnoreCase("")||urlString!=null||!urlString.equalsIgnoreCase("null")) {
                        URL url = new URL(urlString);
                        image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    }else {
                        image = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.logo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    image = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.logo);
                }
                return null;
            }
        }.execute();

        return  imageBmp;
    }



    @Contract("_, _ -> !null")
    public static Drawable drawableFromUrl(String url, Context mContext) throws IOException {

        Bitmap image = null;
        return new BitmapDrawable(getBitmapFromUrl(url,mContext,image));
    }
}
