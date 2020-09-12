package jp.tanikinaapps.chviewer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.squareup.picasso.Picasso;


public class SetDialog extends DialogFragment {

    public static class aboutDialogFragment extends DialogFragment {
        AlertDialog dialog;
        AlertDialog.Builder alert;
        View alertView;
        String version;

        aboutDialogFragment(String version){
            this.version = version;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState){
            alert = new AlertDialog.Builder(getActivity());


            if(getActivity() != null){
                alertView = getActivity().getLayoutInflater().inflate(R.layout.about_dialog,null);
            }

            TextView textTwitter = alertView.findViewById(R.id.twitterAddress);
            textTwitter.setLinksClickable(true);
            textTwitter.setText(Html.fromHtml("<a href=\"https://twitter.com/momorito\">＠momorito<a/>" ));
            textTwitter.setMovementMethod(LinkMovementMethod.getInstance());

            TextView textPolicy = alertView.findViewById(R.id.privacy);
            textPolicy.setLinksClickable(true);
            textPolicy.setText(Html.fromHtml("<a href=\"https://momorito.github.io/privacy.html\">プライバシーポリシー<a/>" ));
            textPolicy.setMovementMethod((LinkMovementMethod.getInstance()));

            TextView aboutClose = alertView.findViewById(R.id.aboutClose);

            alert.setTitle( getResources().getString(R.string.app_name) + " " + version);
            aboutClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            alert.setView(alertView);
            dialog = alert.create();
            dialog.show();

            return dialog;
        }
    }

    public static class SearchDialogFragment extends DialogFragment{
        AlertDialog dialog;
        AlertDialog.Builder alert;
        View alertView;

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState){
            alert = new AlertDialog.Builder(getActivity());


            if(getActivity() != null){
                alertView = getActivity().getLayoutInflater().inflate(R.layout.search_dialog,null);
            }

            Button buttonSearch = alertView.findViewById(R.id.buttonSearch);
            Button buttonCancel = alertView.findViewById(R.id.buttonCancel);
            final RadioGroup radioGroup = alertView.findViewById(R.id.radioGroup);
            final RadioButton buttonArticle = alertView.findViewById(R.id.buttonArticle);
            RadioButton buttonBlog = alertView.findViewById(R.id.buttonBlog);
            final EditText searchText = alertView.findViewById(R.id.searchText);

            buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if(searchText.getText().toString().equals((""))){

                    } else{
                        String[] passData = new String[2];
                        int id = radioGroup.getCheckedRadioButtonId();
                        if(id == buttonArticle.getId()){
                            passData[0] = "article";
                        } else{
                            passData[0] ="blog";
                        }
                        passData[1] = searchText.getText().toString();


                        Intent intent;
                        intent = new Intent(getActivity(),SearchActivity.class);
                        intent.putExtra("jp.tanikinaapps.chviewer.DATA",passData);
                        startActivity(intent);
                    }
                }
            });

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            alert.setTitle(R.string.search);
            alert.setView(alertView);
            dialog = alert.create();
            dialog.show();

            return dialog;
        }
    }

    public static class ImageDialogFragment extends  DialogFragment{
        AlertDialog dialog;
        AlertDialog.Builder alert;
        View alertView;
        String str;

        ImageDialogFragment(String str){
            this.str = str;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState){
            alert = new AlertDialog.Builder(getActivity());


            if(getActivity() != null){
                alertView = getActivity().getLayoutInflater().inflate(R.layout.image_preview_dialog,null);
            }
            ImageView imageView = alertView.findViewById(R.id.imagePreview);

            Button buttonSave = alertView.findViewById(R.id.imageSave);
            Button buttonCancel = alertView.findViewById(R.id.imageCancel);

            int screenWidth,screenHeight;
            screenWidth = 100;
            screenHeight = 100;
            WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            if(wm != null){
                Display disp = wm.getDefaultDisplay();
                Point size = new Point();
                disp.getSize(size);

                screenWidth = size.x;
                screenHeight = (int)(size.y * 0.8);

            }

            Picasso.with(getContext()).load(str).resize(screenWidth,screenHeight).centerInside().into(imageView);

            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(str));
                    startActivity(i);
                }
            });


            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            alert.setTitle("プレビュー");
            alert.setView(alertView);
            dialog = alert.create();
            dialog.show();

            return dialog;
        }
        }
    }

