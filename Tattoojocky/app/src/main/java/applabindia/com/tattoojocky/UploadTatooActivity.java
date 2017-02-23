package applabindia.com.tattoojocky;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import database.DBAdapter;
import database.TattocategoryDB;
import model.ResponseModel;
import model.TattoCatagory;
import model.TattoCatagoryResponse;
import model.TattooInfo;
import model.UserModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utills.CommonProgress;
import utills.CommonUtill;
import utills.Constants;
import utills.Utils;
import webconnectionhandler.ApiClient;
import webconnectionhandler.ApiInterface;

public class UploadTatooActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    @BindView(R.id.spinnerCatg)
    Spinner spinnerCatg;
    @BindView(R.id.imageFrame)
    FrameLayout imageFrame;
    @BindView(R.id.fabaddTatoo)
    FloatingActionButton fabaddTatoo;
    @BindView(R.id.edUploadTattoName)
    EditText edUploadTattoName;
    @BindView(R.id.tattooImg)
    ImageView tattooImg;
    @BindView(R.id.buttonUpload)
    Button buttonUpload;

    DBAdapter dbAdapter;
    TattocategoryDB tattocategoryDB;
    HashMap<String,String> catagMap;
    ArrayList<String> nameList;
    String userId,tattooId;

    private static final int PICK_IMAGE_FROM_CAMERA_REQUEST_ID = 11;
    private static final int PICK_IMAGE_FROM_GALLERY = 10;
    private Uri mFileUri;
    File file;
    Bitmap imageBmp;
    String imagePath = "";
    private CommonProgress progress;


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabaddTatoo:
                selectImage();
                break;
            case R.id.buttonUpload:
                uploadTatoo();
                break;
        }
    }

    private void uploadTatoo() {

        String name = edUploadTattoName.getText().toString();
        if(TextUtils.isEmpty(name)){
            CommonUtill.showSnakbarError(this,"Please enter tattoo name.",edUploadTattoName);
            return;
        }else if(TextUtils.isEmpty(imagePath)){
            CommonUtill.showSnakbarError(this,"Please add tattoo image.",edUploadTattoName);
            return;
        }else{
            doUploadCall(userId,tattooId,name,Uri.parse(imagePath));
        }
    }

    private void doUploadCall(String userId, String tattooId, String name, final Uri fileUri) {
        progress.show("Please wait...");
        // create upload service client
        ApiInterface service =
                ApiClient.getClient().create(ApiInterface.class);

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        MultipartBody.Part body = null;
        if(!fileUri.toString().equals("")) {
            File file = FileUtils.getFile(fileUri.toString());

            String type = CommonUtill.getMimeType(fileUri.toString());
            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(type),
                            file
                    );

            // MultipartBody.Part is used to send also the actual file name
            body =
                    MultipartBody.Part.createFormData("image_file", file.getName(), requestFile);
        }

        //String descriptionString = "user image update";
        // create a map of data to pass along
        RequestBody nameBody = createPartFromString(name);
        RequestBody useridBody = createPartFromString(userId);
        RequestBody catIdBody = createPartFromString(tattooId);



        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("user_id", useridBody);
        map.put("tattoo_name", nameBody);
        map.put("cate_id", catIdBody);
        // add another part within the multipart request

        Call<ResponseModel> call = service.callUploadTattoo(map,body);

        // finally, execute the request
        //  Call<ResponseModel> call = service.updateProfile(user_id,name,email,birthday,gender,contact,
        //          city,state,country,about_me,description, body,"1");
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call,
                                   Response<ResponseModel> response) {

                if (response.isSuccessful()) {
                    ResponseModel userModel = response.body();
                    if (userModel.getStatus().equalsIgnoreCase(Constants.RESPONSE_STATUS_TRUE)) {
                        //do next
                        Log.v("Upload", "success");
                        progress.hide();
                        if(!TextUtils.isEmpty(fileUri.toString())){
                            Utils.deleteFile(new File(fileUri.toString()));

                        }
                        CommonUtill.showSnakbarSucces(UploadTatooActivity.this,"Tattoo updated successfully",imageFrame);
                        setResult(RESULT_OK);
                        finish();

                    }

               /* Log.v("Upload", response.body().getMessage());
                if(response.body().getStatus().equals(Constants.RESPONSE_STATUS_TRUE))
                    Log.v("Upload", "success");
                progress.hide();*/
                    // finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                progress.hide();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_FROM_CAMERA_REQUEST_ID && resultCode == RESULT_OK ){
            imagePath = getImagepath(data);
            Picasso.with(this).load(new File(imagePath)).error(R.drawable.ic_user).into(tattooImg);
        }
        else if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath  = Utils.compressImage(cursor.getString(columnIndex),this);
            File photoFile = new File(imagePath);
            imageBmp = BitmapFactory.decodeFile(
                    photoFile.toString());

            tattooImg.setImageBitmap(imageBmp);

            cursor.close();


        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_tatoo);
        ButterKnife.bind(this);
        init();
        setTattooCtag();
    }

    private void init() {
        progress = new CommonProgress(this);
        userId = getIntent().getStringExtra(Constants.USER_ID);
        spinnerCatg.setOnItemSelectedListener(this);
        fabaddTatoo.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
    }

    private void setTattooCtag() {
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        tattocategoryDB = new TattocategoryDB(this);
        tattocategoryDB.open();
        ArrayList<TattoCatagoryResponse> modelList = tattocategoryDB.getTattooinfo();
        if(modelList!=null&&modelList.size()>0){
            catagMap = new HashMap<>();
            nameList = new ArrayList<>();
            for (TattoCatagoryResponse model:
                    modelList ) {
                catagMap.put(model.getCateName(),model.getCateId());
                nameList.add(model.getCateName());
            }
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nameList);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            spinnerCatg.setAdapter(dataAdapter);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tattocategoryDB!=null)
            tattocategoryDB.close();
        if(dbAdapter!=null)
            dbAdapter.close();
    }





    private String getImagepath(Intent data){

        String image_path ="";

        try {

            if (data != null) {
                System.out.println("SAMSUNGGGG DATA..");
                Uri newuri = data.getData();
                if (newuri == null) {
                    System.out.println("File Length" + file.length());
                    image_path = Utils.compressImage(file.toString(),this);
                    System.out.println("Image path..@@@@@"
                            + image_path);
                    file.delete();

                } else {
                    System.out.println("Uri in data else ...." + file);
                    System.out.println("File Length"
                            + newuri.getPath().length());
                    image_path = Utils.compressImage(newuri.getPath().toString(),this);
                    System.out.println("Image path..@@@@@"
                            + image_path);
                    file.delete();

                }

            } else if (data == null) {
                System.out.println("SAMSUNGGGGGGGGGGGG ....");
                if (file != null) {
                    System.out.println("File Length" + file.length());
                    image_path = Utils.compressImage(file.toString(),this);
                    System.out.println("Image path..@@@@@"
                            + image_path);

                    file.delete();

                }

                else {
                    String[] projection = {
                            MediaStore.Images.Thumbnails._ID, // The
                            // columns
                            // we
                            // want
                            MediaStore.Images.Thumbnails.IMAGE_ID,
                            MediaStore.Images.Thumbnails.KIND,
                            MediaStore.Images.Thumbnails.DATA };
                    String selection = MediaStore.Images.Thumbnails.KIND
                            + "=" + // Select only mini's
                            MediaStore.Images.Thumbnails.MINI_KIND;

                    String sort = MediaStore.Images.Thumbnails._ID
                            + " DESC";

                    // At the moment, this is a bit of a hack, as I'm
                    // returning ALL images, and just taking the latest
                    // one. There is a better way to narrow this down I
                    // think with a WHERE clause which is currently the
                    // selection variable

                    Cursor myCursor = this
                            .managedQuery(
                                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                                    projection, selection, null, sort);

                    long imageId = 0l;
                    long thumbnailImageId = 0l;
                    String thumbnailPath = "";

                    try {
                        myCursor.moveToFirst();
                        imageId = myCursor
                                .getLong(myCursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID));
                        thumbnailImageId = myCursor
                                .getLong(myCursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID));
                        thumbnailPath = myCursor
                                .getString(myCursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
                    } finally {
                        myCursor.close();
                    }

                    // Create new Cursor to obtain the file Path for the
                    // large image

                    String[] largeFileProjection = {
                            MediaStore.Images.ImageColumns._ID,
                            MediaStore.Images.ImageColumns.DATA };

                    String largeFileSort = MediaStore.Images.ImageColumns._ID
                            + " DESC";
                    myCursor = this
                            .managedQuery(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    largeFileProjection, null, null,
                                    largeFileSort);
                    String largeImagePath = "";

                    try {
                        myCursor.moveToFirst();

                        // This will actually give yo uthe file path
                        // location of the image.
                        largeImagePath = myCursor
                                .getString(myCursor
                                        .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                    } finally {
                        myCursor.close();
                    }
                    // These are the two URI's you'll be interested in.
                    // They give you a handle to the actual images
					/*
					 * Uri uriLargeImage =
					 * Uri.withAppendedPath(MediaStore
					 * .Images.Media.EXTERNAL_CONTENT_URI,
					 * String.valueOf(imageId)); Uri uriThumbnailImage =
					 * Uri
					 * .withAppendedPath(MediaStore.Images.Thumbnails.
					 * EXTERNAL_CONTENT_URI,
					 * String.valueOf(thumbnailImageId));
					 *
					 * // I've left out the remaining code, as all I do
					 * is assign the URI's to my own objects anyways...
					 *
					 *
					 *
					 * String[] proj = { MediaStore.Images.Media.DATA };
					 * Cursor cursor = managedQuery(uriLargeImage, proj,
					 * null, null, null); int column_index =
					 * cursor.getColumnIndexOrThrow
					 * (MediaStore.Images.Media.DATA);
					 * cursor.moveToFirst();
					 */
                    image_path = Utils.compressImage(largeImagePath,this);
                    System.out.println("Image path..@@@@@"
                            + image_path);

                    file.delete();
                }

            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(),
                    "Error Processing Image" + e.toString(),
                    Toast.LENGTH_SHORT).show();
            // sentStatus.setText(
            // "Sent Status : Error Processing Image"+e.toString());

        }

        return image_path;
    }


    private void selectImageFromGallery(){
        Intent i = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE_FROM_GALLERY);
    }

    private RequestBody createPartFromString(String data){
        RequestBody dataBody =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, data);
        return dataBody;
    }

    private void startImageCaptureIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Utils.getFile(this);
        mFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri );
        startActivityForResult(intent, PICK_IMAGE_FROM_CAMERA_REQUEST_ID);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    startImageCaptureIntent();
                } else if (items[item].equals("Choose from Library")) {
                    selectImageFromGallery();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        tattooId = catagMap.get(item);
        // Showing selected spinner item
      //  Toast.makeText(parent.getContext(), "Selected: " + item+"::tattooId::"+tattooId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
