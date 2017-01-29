package applabindia.com.tattoojocky.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import applabindia.com.tattoojocky.HomeActivity;
import applabindia.com.tattoojocky.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import database.DBAdapter;
import database.UserinfoDb;
import model.ResponseData;
import model.ResponseModel;
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


public class ProfileEditActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    @BindView(R.id.usrImgProfile) CircularImageView userImg;
    @BindView(R.id.fabCamera) FloatingActionButton addPhoto;
    @BindView(R.id.pager) ViewFlipper frameFlipper;
    @BindView(R.id.page_one) View page_one;
    @BindView(R.id.page_two) View page_two;
    @BindView(R.id.page_three) View page_three;
    @BindView(R.id.page_four) View page_four;
    @BindView(R.id.edProfileEditUserName) EditText edProfileEditUserName;
    @BindView(R.id.edProfileEditEmail) EditText edProfileEditEmail;
    @BindView(R.id.edProfileEditContact) EditText edProfileEditContact;
    @BindView(R.id.edProfileEditCountry) EditText edProfileEditCountry;
    @BindView(R.id.edProfileEditState) EditText edProfileEditState;
    @BindView(R.id.edProfileEditCity) EditText edProfileEditCity;
    @BindView(R.id.edProfileEditAbtMe) EditText edProfileEditAbtMe;
    @BindView(R.id.genderRadio) RadioGroup genderRadioGrp;
    @BindView(R.id.editProfile_radioMale) RadioButton editProfile_radioMale;
    @BindView(R.id.editProfile_radioFemale) RadioButton editProfile_radioFemale;
    @BindView(R.id.editProfile_dob) AppCompatTextView editProfile_dob;
    @BindView(R.id.editProfile_Save) AppCompatTextView editProfile_Save;

    @BindView(R.id.img_next_p1) ImageView nextP1Img;
    @BindView(R.id.img_next_p2) ImageView nextP2Img;
    @BindView(R.id.img_prev_p2) ImageView prevP2Img;
    @BindView(R.id.img_next_p3) ImageView nextP3Img;
    @BindView(R.id.img_prev_p3) ImageView prevP3Img;
    @BindView(R.id.img_prev_p4) ImageView prevP4Img;



    private Calendar calendar ;
    private DatePickerDialog datePickerDialog ;
    private int Year, Month, Day ;
    private String gender = "",aboutMe = "";
    String countryName = "",stateName="",cityName="", user_dob ="";
    String userName ="",userEmail="",userMobNo="",userId ="";
    ResponseData userInfo;
    int ageOfUser = 0;

    private String userChoosenTask;
    private static final int PICK_IMAGE_FROM_CAMERA_REQUEST_ID = 11;
    private static final int PICK_IMAGE_FROM_GALLERY = 10;
    private Uri mFileUri;
    File file;
    Bitmap imageBmp;
    String imagePath = "";
    private CommonProgress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        ButterKnife.bind(this);
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        progress = new CommonProgress(this);


        nextP1Img.setOnClickListener(this);
        nextP2Img.setOnClickListener(this);
        prevP2Img.setOnClickListener(this);
        nextP3Img.setOnClickListener(this);
        prevP3Img.setOnClickListener(this);
        prevP4Img.setOnClickListener(this);
        editProfile_dob.setOnClickListener(this);
        editProfile_Save.setOnClickListener(this);
        editProfile_radioMale.setOnClickListener(this);
        editProfile_radioFemale.setOnClickListener(this);
        addPhoto.setOnClickListener(this);

        userInfo = (ResponseData) getIntent().getSerializableExtra("USER");
        userId = userInfo.getUserId();

        edProfileEditUserName.setText(userInfo.getName());
        edProfileEditEmail.setText(userInfo.getEmail());
        edProfileEditContact.setText(userInfo.getContact()!=null?userInfo.getContact():"");

        edProfileEditCountry.setText(userInfo.getCountry()!=null?userInfo.getCountry():"");
        edProfileEditState.setText(userInfo.getState()!=null?userInfo.getState():"");
        edProfileEditCity.setText(userInfo.getCity()!=null?userInfo.getCity():"");
        if(userInfo.getGender()!=null) {
            boolean genderStatus = userInfo.getGender().equalsIgnoreCase(Constants.MALE) ? true : false;
            if (genderStatus) {
                editProfile_radioMale.setChecked(true);
                editProfile_radioFemale.setChecked(false);
                gender = Constants.MALE;
            } else {
                editProfile_radioFemale.setChecked(true);
                editProfile_radioMale.setChecked(false);
                gender = Constants.FEMALE;
            }
        }
        editProfile_dob.setText(userInfo.getDob()!=null?userInfo.getDob():"Date of Birth");
        edProfileEditAbtMe.setText(userInfo.getAboutMe()!=null?userInfo.getAboutMe():"");

        if (!TextUtils.isEmpty(userInfo.getImage())){
            Picasso.with(this).load(userInfo.getImage()).error(R.drawable.ic_user).into(userImg);
        }else{
            Picasso.with(this).load(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(userImg);
        }


        // 2014-10-20
        if( userInfo.getDob()!=null&& !userInfo.getDob().equals("")&& !userInfo.getDob().equals("null")) {
            String[] dateString = userInfo.getDob().split("-");
            ageOfUser = CommonUtill.getAge(Integer.parseInt(dateString[0]),Integer.parseInt(dateString[1]),Integer.parseInt(dateString[2]));
            user_dob = dateString[1]+"/"+dateString[2]+"/"+dateString[0];
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editProfile_dob:
                datePickerDialog = DatePickerDialog.newInstance(ProfileEditActivity.this, Year, Month, Day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setAccentColor(Color.parseColor("#009688"));
                datePickerDialog.setTitle("Select Date of Birth");
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
                break;
            case R.id.img_next_p1:
                goTo2ndPage();
                break;
            case R.id.img_next_p2:
                goTo3rdPage();
                break;
            case R.id.img_prev_p2:
                showView(Constants.EDIT_P1,false);
                break;
            case R.id.img_next_p3:
                goTo4thPage();
                break;
            case R.id.img_prev_p3:
                showView(Constants.EDIT_P2,false);
                break;
            case R.id.img_prev_p4:
                showView(Constants.EDIT_P3,false);
                break;
            case R.id.editProfile_Save:
                saveProfile();
                break;
            case R.id.editProfile_radioMale:
                editProfile_radioFemale.setChecked(false);
                editProfile_radioMale.setChecked(true);
                gender = Constants.MALE;

                break;
            case R.id.editProfile_radioFemale:
                editProfile_radioFemale.setChecked(true);
                editProfile_radioMale.setChecked(false);
                gender = Constants.FEMALE;
                break;
            case R.id.fabCamera:
                selectImage();
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "-" + (monthOfYear+1) + "-" + year;
        editProfile_dob.setText(date);
        //  birthday	10/20/1985
        user_dob = (monthOfYear+1)+"/"+dayOfMonth+"/"+year;
        ageOfUser = CommonUtill.getAge(year,(monthOfYear+1),dayOfMonth);
    }

    private void showView(int displayChild,boolean nextPrev) {

        if(nextPrev) {
            frameFlipper.setInAnimation(this, R.anim.in_from_right);
            frameFlipper.setOutAnimation(this, R.anim.out_to_left);
        }else{
            frameFlipper.setInAnimation(this, R.anim.in_from_left);
            frameFlipper.setOutAnimation(this, R.anim.out_to_right);
        }
        frameFlipper.setDisplayedChild(displayChild);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.editProfile_radioMale:
                editProfile_radioFemale.setChecked(false);
                gender = Constants.MALE;

                break;
            case R.id.editProfile_radioFemale:
                editProfile_radioMale.setChecked(false);
                gender = Constants.FEMALE;
                break;
        }
    }


    //Declaration
    private class CustomTextWatcher implements TextWatcher {

        private View view;
        private CustomTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch(view.getId()){
                case R.id.edProfileEditContact:

                    break;
                case R.id.email:

                    break;

            }
        }
    }


    private void goTo2ndPage(){
        userName = edProfileEditUserName.getText().toString();
        userEmail = edProfileEditEmail.getText().toString();
        userMobNo = edProfileEditContact.getText().toString();
        if(TextUtils.isEmpty(userName)){
            CommonUtill.showSnakbarError(this,"Enter user name.",edProfileEditUserName);
            return;
        }else if(TextUtils.isEmpty(userEmail)||!userEmail.contains("@")){
            CommonUtill.showSnakbarError(this,"Enter email.",edProfileEditEmail);
            return;
        }else if(TextUtils.isEmpty(userMobNo)){
            CommonUtill.showSnakbarError(this,"Enter mobile no.",edProfileEditContact);
            return;
        }else if(userMobNo.trim().length()<10){
            CommonUtill.showSnakbarError(this,"Enter valid mobile no.",edProfileEditContact);
            return;
        }else{
            showView(Constants.EDIT_P2,true);
        }
    }
    private void goTo3rdPage(){
        // user_dob = editProfile_dob.getText().toString();

        if(TextUtils.isEmpty(gender)){
            CommonUtill.showSnakbarError(this,"Please choose gender",genderRadioGrp);
            return;
        }else if(TextUtils.isEmpty(user_dob)){
            CommonUtill.showSnakbarError(this,"Enter date of birth.",editProfile_dob);
            return;
        }else if(ageOfUser <18){
            CommonUtill.showSnakbarError(this,"Age must be greater than 18.",editProfile_dob);
            return;
        }else{
            showView(Constants.EDIT_P3,true);
        }
    }
    private void goTo4thPage(){
        countryName = edProfileEditCountry.getText().toString();
        stateName = edProfileEditState.getText().toString();
        cityName = edProfileEditCity.getText().toString();
        if(TextUtils.isEmpty(countryName)){
            CommonUtill.showSnakbarError(this,"Enter country name.",edProfileEditCountry);
            return;
        }else if(TextUtils.isEmpty(stateName)){
            CommonUtill.showSnakbarError(this,"Enter state.",edProfileEditState);
            return;
        }else if(TextUtils.isEmpty(cityName)){
            CommonUtill.showSnakbarError(this,"Enter city.",edProfileEditCity);
            return;
        }else{
            showView(Constants.EDIT_P4,true);
        }
    }


    private void saveProfile(){
        aboutMe = edProfileEditAbtMe.getText().toString();
        uploadFile(userId,userName,userEmail, user_dob,gender,userMobNo,cityName,stateName,countryName,aboutMe,Uri.parse(imagePath));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void uploadFile(String user_id,
                            String name,
                            String email,
                            String birthday,
                            String gender,
                            String contact,
                            String city,
                            String state,
                            String country,
                            String about_me,
                            final Uri fileUri) {


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
        RequestBody emailBody = createPartFromString(email);
        RequestBody bodBody = createPartFromString(birthday);
        RequestBody genderBody = createPartFromString(gender);
        RequestBody contactBody = createPartFromString(contact);
        RequestBody cityBody = createPartFromString(city);
        RequestBody stateBody = createPartFromString(state);
        RequestBody countryBody = createPartFromString(country);
        RequestBody abtmeBody = createPartFromString(about_me);
        RequestBody useridBody = createPartFromString(user_id);
        RequestBody statusBody = createPartFromString("1");



        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("user_id", useridBody);
        map.put("name", nameBody);
        map.put("email", emailBody);
        map.put("birthday", bodBody);
        map.put("gender", genderBody);
        map.put("contact", contactBody);
        map.put("city", cityBody);
        map.put("state", stateBody);
        map.put("country", countryBody);
        map.put("about_me", abtmeBody);
        map.put("status", statusBody);
        // add another part within the multipart request

        Call<UserModel> call = service.callUpdateProfile(map,body);

        // finally, execute the request
        //  Call<ResponseModel> call = service.updateProfile(user_id,name,email,birthday,gender,contact,
        //          city,state,country,about_me,description, body,"1");
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call,
                                   Response<UserModel> response) {

                if (response.isSuccessful()) {
                    UserModel userModel = response.body();
                    if (userModel.getStatus().equalsIgnoreCase(Constants.RESPONSE_STATUS_TRUE)) {
                        //do next
                        updateUserInfo(userModel.getResponseData());
                        progress.hide();
                        if(!TextUtils.isEmpty(fileUri.toString())){
                            Utils.deleteFile(new File(fileUri.toString()));
                        }

                    }

               /* Log.v("Upload", response.body().getMessage());
                if(response.body().getStatus().equals(Constants.RESPONSE_STATUS_TRUE))
                    Log.v("Upload", "success");
                progress.hide();*/
                    // finish();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                progress.hide();
            }
        });
    }

    private void updateUserInfo(ResponseData data) {
        DBAdapter dbAdapter = null;
        UserinfoDb userinfoDb = null;
        if(data!=null) {
            try {
                dbAdapter = new DBAdapter(this);
                dbAdapter.open();
                userinfoDb = new UserinfoDb(this);
                userinfoDb.open();
                boolean status = userinfoDb.updateUserinfo(data);
                Log.i("update status ::",""+status);
                if (status) {
                    CommonUtill.showSnakbarSucces(ProfileEditActivity.this,"Profile updated successfully",frameFlipper);
                    sendBroadcast(new Intent(HomeActivity.PROFILE_UPDATED_ACTION));
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();

            }finally {
                if(userinfoDb!=null)
                    userinfoDb.close();
                if(dbAdapter!=null)
                    dbAdapter.close();
            }
        }
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    startImageCaptureIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    selectImageFromGallery();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void startImageCaptureIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Utils.getFile(this);
        mFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri );
        startActivityForResult(intent, PICK_IMAGE_FROM_CAMERA_REQUEST_ID);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_FROM_CAMERA_REQUEST_ID && resultCode == RESULT_OK ){
			/*if(mFileUri == null){
				mFileUri = Uri.fromFile(getFile(this));
			}
			String path = mFileUri.getPath();*/
            imagePath = getImagepath(data);
            Picasso.with(this).load(new File(imagePath)).error(R.drawable.ic_user).into(userImg);
            // Utility.getBitmapFromFile(imagePath,img);

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
           /* BitmapFactory.Options options;
            if (photoFile.length() > 1000000) {
                options = new BitmapFactory.Options();
                options.inSampleSize = 4;

            } else {
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;

            }

            imageBmp = BitmapFactory.decodeFile(
                    photoFile.toString(), options);

            //							System.out.println("image size1......."
            //									+ imageBmp.getByteCount());

            if (imageBmp != null) {

                imagePath = fixOrientation(imageBmp,photoFile.toString());
                System.out.println("Image path gallery..@@@@@"
                        + imagePath);

            }*/

            userImg.setImageBitmap(imageBmp);

            cursor.close();


        }
    }


    private void selectImageFromGallery(){
        Intent i = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE_FROM_GALLERY);
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





    private RequestBody createPartFromString(String data){
        RequestBody dataBody =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, data);
        return dataBody;
    }

}
