package com.example.makeyservice.makeydistribution.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.makeyservice.makeydistribution.APISqlite.DatabaseHandler;
import com.example.makeyservice.makeydistribution.APiRest.ApiClient;
import com.example.makeyservice.makeydistribution.APiRest.ApiInterface;
import com.example.makeyservice.makeydistribution.Activity.ListOCRActivity;
import com.example.makeyservice.makeydistribution.Model.Adresse;
import com.example.makeyservice.makeydistribution.Model.GeoAdresse;
import com.example.makeyservice.makeydistribution.Outils.CropImageViewOptions;
import com.example.makeyservice.makeydistribution.Outils.ProgressBar;
import com.example.makeyservice.makeydistribution.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by makeyservice on 28/06/2017.
 */

public class ImageScannerFragment extends Fragment
        implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener {

    private InterfaceCrop mDemoPreset;
    private CropImageView mCropImageView;
    File file;
    Button btnanalyser;
    ProgressBar progressBar;
    private ApiClient apiClient;
    SharedPreferences sharedpreferences;

    @BindView(R.id.editAdresse)
    EditText adresseTExt;
    @BindView(R.id.editAdressec)
    EditText adresseCompl;
    @BindView(R.id.editcP)
    EditText codePostal;
    @BindView(R.id.editNomp)
    EditText nomPrenom;
    @BindView(R.id.editVille)
    EditText ville;
    @BindView(R.id.imageResultat)
    ImageView imageResultat;

    @BindView(R.id.txtResultat)
    TextView txtResultat;
    @BindView(R.id.LinerLayoutResultat)
    LinearLayout LinerLayoutResultat;
    Bitmap mImage;
    @BindView(R.id.textView_Title)
    TextView textView_Title;
    @BindView(R.id.editInfoPortage)
    EditText editInfoPortage;
    @BindView(R.id.btnSupp)
    Button btnSupp;
    OkHttpClient.Builder okHttpClient;
    Retrofit retrofit;
    ApiInterface api_;
    @BindView(R.id.btnEnregistrementAnalyser)
    Button btnEnregistrementAnalyser;
    DatabaseHandler db ;
    File filedir;
    @BindView(R.id.resultImageText)
    TextView resultImageText;
    @BindView(R.id.hScroll)
    HorizontalScrollView hScroll;


    public ImageScannerFragment newInstance(InterfaceCrop demoPreset) {
        ImageScannerFragment fragment = new ImageScannerFragment();
        Bundle args = new Bundle();
        args.putString("DEMO_PRESET", demoPreset.name());
        fragment.setArguments(args);
        return fragment;
    }
    public ImageScannerFragment addBitmap(File file,File filedir) {
        if (file != null)
            this.file = file;
        this.filedir = filedir;
        return this;
    }

    public void updateCurrentCropViewOptions() {
        CropImageViewOptions options = new CropImageViewOptions();
        options.scaleType = mCropImageView.getScaleType();
        options.cropShape = mCropImageView.getCropShape();
        options.guidelines = mCropImageView.getGuidelines();
        options.aspectRatio = mCropImageView.getAspectRatio();
        options.fixAspectRatio = mCropImageView.isFixAspectRatio();
        options.showCropOverlay = mCropImageView.isShowCropOverlay();
        options.showProgressBar = mCropImageView.isShowProgressBar();
        options.autoZoomEnabled = mCropImageView.isAutoZoomEnabled();
        options.maxZoomLevel = mCropImageView.getMaxZoom();
        //options.flipHorizontally = mCropImageView.isFlippedHorizontally();
        //options.flipVertically = mCropImageView.isFlippedVertically();
        //((MainActivity) getActivity()).setCurrentOptions(options);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        switch (mDemoPreset) {
            case RECT:
                rootView = inflater.inflate(R.layout.imagescanner, container, false);
                btnanalyser  = (Button) rootView.findViewById(R.id.btnAnalyserCrop);
                break;

            default:
                throw new IllegalStateException("Unknown preset: " + mDemoPreset);
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiClient = new ApiClient();
        progressBar = new ProgressBar(getActivity(),"Makey Distribution","Chargement...");
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        mCropImageView = (CropImageView) view.findViewById(R.id.cropImageView);
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
        setHasOptionsMenu(true);
        updateCurrentCropViewOptions();
        db = new DatabaseHandler(getActivity());
        if (savedInstanceState == null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            mCropImageView.setImageBitmap(myBitmap);
            mCropImageView.setBackgroundResource(R.drawable.backdrop);
        }
        okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                String authorization = new StringBuilder("Bearer ")
                        .append(sharedpreferences.getString("acces_token",null)).toString();
                Request.Builder newRequest = request.newBuilder()
                        .addHeader("Content-Type","Application/json")
                        .addHeader("Authorization",authorization);
                return chain.proceed(newRequest.build());
            }
        });
        retrofit = apiClient.getClientWithOkHttp(sharedpreferences.getString("url",null),
                okHttpClient);
        api_ = retrofit.create(ApiInterface.class);

        btnanalyser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.Show();
                mCropImageView.getCroppedImageAsync();
            }
        });
        btnEnregistrementAnalyser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.Show();
                btnEnregAnalyser();

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_action_rotate) {
            mCropImageView.rotateImage(90);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDemoPreset = InterfaceCrop.valueOf(getArguments().getString("DEMO_PRESET"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCropImageView != null) {
            mCropImageView.setOnSetImageUriCompleteListener(null);
            mCropImageView.setOnCropImageCompleteListener(null);
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            Toast.makeText(getActivity(), "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("AIC", "Failed to load image by URI", error);
            Toast.makeText(getActivity(), "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            handleCropResult(result);
        }
    }

    private void handleCropResult(CropImageView.CropResult result) {
        if (result.getError() == null) {
            mImage = mCropImageView.getCropShape() == CropImageView.CropShape.OVAL
                    ? CropImage.toOvalBitmap(result.getBitmap())
                    : result.getBitmap();
            getDataTexte(mImage,detectText(mImage));
        } else {
            Log.e("AIC", "Failed to crop image", result.getError());
            Toast.makeText(getActivity(), "Image crop failed: " + result.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //API OCR GOOGLE
    public String detectText(Bitmap bitmap) {
        String blocks = "";
        String lines = "";
        String words = "";
        TextRecognizer detector = null;
        try {
            detector = new TextRecognizer.Builder(getContext()).build();
            if (detector.isOperational() && bitmap != null) {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> textBlocks = detector.detect(frame);
                for (int index = 0; index < textBlocks.size(); index++) {
                    //extract scanned text blocks here
                    TextBlock tBlock = textBlocks.valueAt(index);
                    blocks = blocks + tBlock.getValue() + "\n" + "\n";
                    for (Text line : tBlock.getComponents()) {
                        //extract scanned text lines here
                        lines = lines + line.getValue() + "\n";
                        for (Text element : line.getComponents()) {
                            //extract scanned text words here
                            words = words + element.getValue() + ", ";
                        }
                    }
                }
                if (textBlocks.size() == 0) {
                    Log.e("maoris ", "Scan Failed: Found nothing to scan");
                    //scanResults.setText("Scan Failed: Found nothing to scan");
                } else {
                    /*Log.e("maoris ", "Blocks: " + "\n");
                    Log.e("maoris ", ""+ blocks + "\n");
                    Log.e("maoris ", "---------" + "\n");
                    Log.e("maoris ", " "+lines+" \n");
                    Log.e("maoris ", "---------" + "\n");
                    Log.e("maoris ", " "+words+ "\n");
                    Log.e("maoris ", "---------" + "\n");*/
                }
            } else {
                Log.e("maoris ","Could not set up the detector!");
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to load Image", Toast.LENGTH_SHORT)
                    .show();
            Log.e("maoris ", e.toString());
        }
        return lines;
    }

    private void getDataTexte(Bitmap bmp,String text){
        if(!text.isEmpty()){
            resultImageText.setText(text);
            resultImageText.setVisibility(View.VISIBLE);
        }

        Call<Adresse> call_ = null;
        call_ =api_.getUpload(text,"android");
        call_.enqueue(new Callback<Adresse>() {
            @Override
            public void onResponse(Call<Adresse> call, Response<Adresse> response) {
                if(!response.equals(null)){
                    Adresse adresse =  response.body();
                    if(adresse !=null){
                        adresseTExt.setText(adresse.getAdresse());
                        ville.setText(adresse.getVille());
                        codePostal.setText(adresse.getCpVille());
                        nomPrenom.setText(adresse.getNomPrenom());
                        adresseCompl.setText(adresse.getComplementAdresse());
                        editInfoPortage.setText(adresse.getInfoPortage());
                        if(!codePostal.getText().toString().isEmpty() || !nomPrenom.getText().toString().isEmpty()
                                || !adresseCompl.getText().toString().isEmpty() || !ville.getText().toString().isEmpty()
                                || !adresseTExt.getText().toString().isEmpty()
                                || !editInfoPortage.getText().toString().isEmpty() ){
                            textView_Title.setText(" Corriger les informations si nécessaire ");
                            btnSupp.setClickable(true);

                        }else{
                            textView_Title.setText(" Saisir les informations ");
                            Toast.makeText(getContext(),"Veuillez réessayer avec une autre position" +
                                    " svp!",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(),"Veuillez réessayer avec une autre position svp!",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Log.d("test","test vide "+response.body());
                }
                progressBar.Dismiss();
                //dismiss();
            }

            @Override
            public void onFailure(Call<Adresse> call, Throwable t) {
                progressBar.Dismiss();
                Log.d("test ocr"," fail "+t.getMessage());
            }
        });
    }
    @OnClick(R.id.btnSupp)
    public void OnSupp(){
        adresseTExt.setText("");
        ville.setText("");
        codePostal.setText("");
        nomPrenom.setText("");
        adresseCompl.setText("");
        editInfoPortage.setText("");
        btnSupp.setClickable(false);
    }

    public void btnEnregAnalyser(){
        Call<GeoAdresse> call = api_.getGeoAdresse(adresseTExt.getText().toString()
                ,codePostal.getText().toString(),ville.getText().toString());
        call.clone().enqueue(new Callback<GeoAdresse>() {
            @Override
            public void onResponse(Call<GeoAdresse> call, Response<GeoAdresse> response) {
                Log.d("test",""+response.body());
                String txtHtml = "<b><i><u> Résultat trouvé </u></i></b>";
                String score = "";
                if(!response.body().getScore().isNaN()){
                    score = String.valueOf((response.body().getScore()*100));
                }
                String adresseHtml = response.body().getHousenumber()+" "+response.body().getStreet()
                        +"&nbsp;&nbsp;&nbsp;&nbsp;"+"Pertinence : "+score+" %";
                String cp = String.valueOf(response.body().getPostcode());

                if (Build.VERSION.SDK_INT >= 24) {
                    txtResultat.setText(Html.fromHtml(txtHtml,Html.FROM_HTML_MODE_COMPACT)+"\n"
                            +Html.fromHtml(adresseHtml,Html.FROM_HTML_MODE_COMPACT)+"\n"
                            +cp);
                } else {
                    txtResultat.setText(Html.fromHtml(txtHtml)+"\n"
                            +Html.fromHtml(adresseHtml)+"\n"
                            +cp);
                }

                LinerLayoutResultat.setVisibility(View.VISIBLE);
                imageResultat.setImageBitmap(mImage);
                imageResultat.setBackgroundResource(R.drawable.backdrop);
                GeoAdresse geoAdresse = new GeoAdresse();
                geoAdresse.setStreet(response.body().getStreet());
                geoAdresse.setPostcode(response.body().getPostcode());
                geoAdresse.setHousenumber(response.body().getHousenumber());
                geoAdresse.setScore(response.body().getScore());
                geoAdresse.setChemin(""+deleteAndAddFile(file,filedir,mImage));
                db.addGeoAdresse(geoAdresse);
                progressBar.Dismiss();
            }

            @Override
            public void onFailure(Call<GeoAdresse> call, Throwable t) {
                Log.d("test",""+t.getMessage());
                progressBar.Dismiss();
            }
        });
    }
    @OnClick(R.id.LinerLayoutResultat)
    public void OnDismiss(){
        LinerLayoutResultat.setVisibility(View.GONE);
    }
    @OnClick(R.id.resultImageText)
    public void OnDismissText(){
        resultImageText.setVisibility(View.GONE);
    }

    private File deleteAndAddFile(File fileold,File fileNew,Bitmap bitmap){
        final File file = new File(fileNew+"/scanner_"+new
                SimpleDateFormat("HH:mm:ss_yyyy_MM_dd").format(new Date())+".jpg");
        if(fileold.exists()){
            fileold.delete();

        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // JPEG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @OnClick(R.id.btnListe)
    public void OnCall(){
        startActivity(new Intent(getActivity(), ListOCRActivity.class));
    }


    @OnClick(R.id.right_nav)
    public void OnClickRight(){
        hScroll.scrollTo((int)hScroll.getScrollX() + 80, (int)hScroll.getScrollY());
    }

    @OnClick(R.id.left_nav)
    public void OnClickLeft(){
        hScroll.scrollTo((int)hScroll.getScrollX() - 80, (int)hScroll.getScrollY());
    }

    public  int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public  int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
