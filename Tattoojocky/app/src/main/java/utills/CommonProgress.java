package utills;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Alok on 28-01-2017.
 */
public class CommonProgress{

    ProgressDialog dlg;
    Context ctx;


   public  CommonProgress(Context ctx){
        this.ctx=ctx;
        dlg = new ProgressDialog(ctx);
        dlg.setCancelable(false);
    }

    public void show(String msg){
        dlg.setMessage(msg);
        dlg.show();
    }

    public void hide(){
        if (dlg.isShowing()){
            dlg.dismiss();
        }
    }
}
