package utills;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ThreadPoolExecutor;

import applabindia.com.tattoojocky.R;

/**
 * Created by Alok on 30-09-2016.
 */
public class CommonUtill {

    private static Bitmap imageBitmap;
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
        snackbar.getView().setBackgroundResource(R.color.colorPrimary);
        TextView v = (TextView) snackbar.getView().findViewById(android.R.id.message);

        if (v != null) {
            v.setGravity(Gravity.CENTER);
            v.setTextColor(context.getResources().getColor(R.color.white));
        }
        snackbar.show();
    }


    public static void showSnakbarError(Context context, String message, View anchor) {
        Snackbar snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.orange);
        TextView v = (TextView) snackbar.getView().findViewById(android.R.id.message);

        if (v != null) {
            v.setGravity(Gravity.CENTER);
            v.setTextColor(context.getResources().getColor(R.color.white));
        }
        snackbar.show();
    }


    public static Bitmap getBitmapFromUrl(final String urlString,final Context mContext) {

        new AsyncTask<Void,Void,Bitmap>(){
            Bitmap image = null;
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                imageBitmap = bitmap;
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
                return image;
            }
        }.execute();


        return  imageBitmap;
    }



    @Contract("_, _ -> !null")
    public static Drawable drawableFromUrl(String url, Context mContext) throws IOException {

        Bitmap image = null;
        return new BitmapDrawable(getBitmapFromUrl(url,mContext));
    }


    public static ShareLinkContent share(String path){

        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentTitle("TattooJockey")
                .setContentDescription("For more explore, please visit")
                .setContentUrl(Uri.parse("http://tattoojockey.com/"))
                .setImageUrl(Uri.parse(path))
                .build();

       return shareLinkContent;
        /*Intent intent = context.getPackageManager().getLaunchIntentForPackage(application);
        if (intent != null) {
            // The application exists
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(application);
            Uri screenshotUri = Uri.parse(path);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
           // shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, title);
          //  shareIntent.putExtra(Intent.EXTRA_TEXT, description);
            // Start the specific social application
            context.startActivity(shareIntent);
        } else {
            // The application does not exist
            // Open GooglePlay or use the default system picker
        }*/
    }

    public static void shareItem(final Context ctx, String url, final String application, final String title) {
        Picasso.with(ctx.getApplicationContext()).load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
               /* Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image*//*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(ctx,bitmap));
                ctx.startActivity(Intent.createChooser(i, "Share Image"));*/
                Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(application);
                if (intent != null) {
                    // The application exists
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setPackage(application);
                    Uri screenshotUri = getLocalBitmapUri(ctx,bitmap);
                    shareIntent.setType("image/png");
                   // shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                    shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, title);
                   // shareIntent.putExtra(Intent.EXTRA_TEXT, "For more explore, please visit http://tattoojockey.com/");
                    // Start the specific social application
                    ctx.startActivity(shareIntent);
                } else {
                    // The application does not exist
                    // Open GooglePlay or use the default system picker
                }
            }
            @Override public void onBitmapFailed(Drawable errorDrawable) { }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }

    public static Uri getLocalBitmapUri(Context ctx,Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    public static int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        return ageInt;
    }


    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
