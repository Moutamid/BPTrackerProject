package com.moutamid.bptracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.bptracker.ui.charts.ChartsFragment;
import com.tejpratapsingh.pdfcreator.activity.PDFCreatorActivity;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tejpratapsingh.pdfcreator.views.PDFBody;
import com.tejpratapsingh.pdfcreator.views.PDFFooterView;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;
import com.tejpratapsingh.pdfcreator.views.PDFTableView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFHorizontalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFImageView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFLineSeparatorView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PdfCreater extends PDFCreatorActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ArrayList<ReadingModel> readingsArrayList = new ArrayList<>();

    private ArrayList<Integer> systolicIntsList = new ArrayList<>();
    private ArrayList<String> dateStringsList = new ArrayList<>();
    private ArrayList<Integer> diastolicIntsList = new ArrayList<>();
    private ArrayList<Integer> pulseIntsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        String profileKey = utils.getStoredString(getApplicationContext(), "currentProfileKey");

        databaseReference.child("readings").child(mAuth.getCurrentUser().getUid())
                .child(profileKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (getActivity() == null) {
//                            Log.d(TAG, "onDataChange: " + "activity is null");
//                            return;
//                        }

                        if (!snapshot.exists()) {
//                            if (!getActivity().isDestroyed())
//                            progressDialog.dismiss();
                            return;
                        }
                        readingsArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            readingsArrayList.add(dataSnapshot.getValue(ReadingModel.class));
                        }

                        extractReadingsLists();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
//                        if (!getActivity().isDestroyed())
//                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void extractReadingsLists() {

        systolicIntsList.clear();
        diastolicIntsList.clear();
        pulseIntsList.clear();
        dateStringsList.clear();

        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            dateStringsList.add(readingsArrayList.get(i).getDate());
        }

        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            pulseIntsList.add(Integer.parseInt(readingsArrayList.get(i).getPulse()));
        }

        // EXTRACT DIASTOLIC LISTS
        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            diastolicIntsList.add(Integer.parseInt(readingsArrayList.get(i).getDiastolic()));
        }

        // EXTRACT SYSTOLIC LISTS
        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            systolicIntsList.add(Integer.parseInt(readingsArrayList.get(i).getSystolic()));
        }

        createPDF("Patient report", new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                Toast.makeText(PdfCreater.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(PdfCreater.this, "PDF NOT Created", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private static class ReadingModel {

        private String date, cuffLocation, bodyPosition, systolic,
                diastolic, pulse, weight, spo2,
                glucose, status, pp, pushKey, userUid, map;

        private int color;

        public ReadingModel(String date, String cuffLocation, String bodyPosition, String systolic, String diastolic, String pulse, String weight, String spo2, String glucose, String status, String pp, String pushKey, String userUid, String map, int color) {
            this.date = date;
            this.cuffLocation = cuffLocation;
            this.bodyPosition = bodyPosition;
            this.systolic = systolic;
            this.diastolic = diastolic;
            this.pulse = pulse;
            this.weight = weight;
            this.spo2 = spo2;
            this.glucose = glucose;
            this.status = status;
            this.pp = pp;
            this.pushKey = pushKey;
            this.userUid = userUid;
            this.map = map;
            this.color = color;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getMap() {
            return map;
        }

        public void setMap(String map) {
            this.map = map;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getCuffLocation() {
            return cuffLocation;
        }

        public void setCuffLocation(String cuffLocation) {
            this.cuffLocation = cuffLocation;
        }

        public String getBodyPosition() {
            return bodyPosition;
        }

        public void setBodyPosition(String bodyPosition) {
            this.bodyPosition = bodyPosition;
        }

        public String getSystolic() {
            return systolic;
        }

        public void setSystolic(String systolic) {
            this.systolic = systolic;
        }

        public String getDiastolic() {
            return diastolic;
        }

        public void setDiastolic(String diastolic) {
            this.diastolic = diastolic;
        }

        public String getPulse() {
            return pulse;
        }

        public void setPulse(String pulse) {
            this.pulse = pulse;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getSpo2() {
            return spo2;
        }

        public void setSpo2(String spo2) {
            this.spo2 = spo2;
        }

        public String getGlucose() {
            return glucose;
        }

        public void setGlucose(String glucose) {
            this.glucose = glucose;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPp() {
            return pp;
        }

        public void setPp(String pp) {
            this.pp = pp;
        }

        public String getPushKey() {
            return pushKey;
        }

        public void setPushKey(String pushKey) {
            this.pushKey = pushKey;
        }

        public String getUserUid() {
            return userUid;
        }

        public void setUserUid(String userUid) {
            this.userUid = userUid;
        }

        ReadingModel() {
        }
    }

    @Override
    protected PDFHeaderView getHeaderView(int pageIndex) {
        PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());

        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());

        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.HEADER);
        SpannableString word = new SpannableString("Blood Pressure Report");
        word.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pdfTextView.setText(word);
        pdfTextView.setLayout(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        pdfTextView.getView().setGravity(Gravity.CENTER_VERTICAL);
        pdfTextView.getView().setTypeface(pdfTextView.getView().getTypeface(), Typeface.BOLD);

        horizontalView.addView(pdfTextView);

        PDFImageView imageView = new PDFImageView(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParam = new LinearLayout.LayoutParams(
                60,
                60, 0);
        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.mipmap.ic_launcher);
        imageLayoutParam.setMargins(0, 0, 10, 0);
        imageView.setLayout(imageLayoutParam);

        horizontalView.addView(imageView);

        headerView.addView(horizontalView);

        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        headerView.addView(lineSeparatorView1);

        return headerView;
    }

    private Utils utils = new Utils();

    @Override
    protected PDFBody getBodyViews() {
        PDFBody pdfBody = new PDFBody();

        PDFTextView pdfCompanyNameView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        pdfCompanyNameView.setText("Patient Name: " + utils.getStoredString(getApplicationContext(), "currentProfileName"));
        pdfBody.addView(pdfCompanyNameView);
        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        pdfBody.addView(lineSeparatorView1);
        PDFTextView pdfAddressView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfAddressView.setText("Report date: " + utils.getDate());
        pdfBody.addView(pdfAddressView);

        PDFLineSeparatorView lineSeparatorView2 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        lineSeparatorView2.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0));
        pdfBody.addView(lineSeparatorView2);

        PDFLineSeparatorView lineSeparatorView3 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        pdfBody.addView(lineSeparatorView3);

        int[] widthPercent = {20, 20, 20, 40}; // Sum should be equal to 100%
        String[] textInTable = {"Date", "Systolic", "Diastolic", "Pulse"};
        PDFTextView pdfTableTitleView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfTableTitleView.setText("Blood pressure readings");
        pdfBody.addView(pdfTableTitleView);

        PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
        for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView.setText(s);
            tableHeader.addToRow(pdfTextView);
        }

        PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());

        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfTextView.setText(dateStringsList.get(0));
        tableRowView1.addToRow(pdfTextView);

        PDFTextView pdfTextView10 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfTextView10.setText(systolicIntsList.get(0).toString());
        tableRowView1.addToRow(pdfTextView10);

        PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfTextView2.setText(diastolicIntsList.get(0).toString());
        tableRowView1.addToRow(pdfTextView2);

        PDFTextView pdfTextView3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfTextView3.setText(pulseIntsList.get(0).toString());
        tableRowView1.addToRow(pdfTextView3);

//        for (String s : textInTable) {
//            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//            pdfTextView.setText("Row 1 : " + s);
//            tableRowView1.addToRow(pdfTextView);
//        }

//        for (int i = 1; i <= 4; i++) {
//            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//            pdfTextView.setText("Row 1 : " + s);
//            tableRowView1.addToRow(pdfTextView);
//        }

        PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);

        for (int i = 1; i <= readingsArrayList.size() - 1; i++) {

            PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
//            for (int i2 = 1; i2 <= 4; i2++) {
            PDFTextView dateTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            dateTextView.setText(dateStringsList.get(i));
            tableRowView.addToRow(dateTextView);

            PDFTextView sysTextView10 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            sysTextView10.setText(systolicIntsList.get(i).toString());
            tableRowView.addToRow(sysTextView10);

            PDFTextView diasTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            diasTextView2.setText(diastolicIntsList.get(i).toString());
            tableRowView.addToRow(diasTextView2);

            PDFTextView pulseTextView3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pulseTextView3.setText(pulseIntsList.get(i).toString());
            tableRowView.addToRow(pulseTextView3);
//            }
            tableView.addRow(tableRowView);
        }
        tableView.setColumnWidth(widthPercent);
        pdfBody.addView(tableView);

        PDFLineSeparatorView lineSeparatorView4 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        pdfBody.addView(lineSeparatorView4);

//        PDFTextView pdfIconLicenseView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
//        Spanned icon8Link = Html.fromHtml("Icon from <a href='https://icons8.com'>https://icons8.com</a>");
//        pdfIconLicenseView.getView().setText(icon8Link);
//        pdfBody.addView(pdfIconLicenseView);

        return pdfBody;
    }

    @Override
    protected PDFFooterView getFooterView(int pageIndex) {
        PDFFooterView footerView = new PDFFooterView(getApplicationContext());

        PDFTextView pdfTextViewPage = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", pageIndex + 1));
        pdfTextViewPage.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0));
        pdfTextViewPage.getView().setGravity(Gravity.CENTER_HORIZONTAL);

        footerView.addView(pdfTextViewPage);

        return footerView;
    }

    @Nullable
    @Override
    protected PDFImageView getWatermarkView(int forPage) {
        PDFImageView pdfImageView = new PDFImageView(getApplicationContext());
        FrameLayout.LayoutParams childLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200, Gravity.CENTER);
        pdfImageView.setLayout(childLayoutParams);

        pdfImageView.setImageResource(R.drawable.ic_baseline_bar_chart_24);
        pdfImageView.setImageScale(ImageView.ScaleType.FIT_CENTER);
        pdfImageView.getView().setAlpha(0.0F);

        return pdfImageView;
    }

    @Override
    protected void onNextClicked(final File savedPDFFile) {
        Uri pdfUri1 = Uri.fromFile(savedPDFFile);

        Uri pdfUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getApplicationContext().getPackageName() + ".provider", savedPDFFile);

//        Intent intentPdfViewer = new Intent(PdfCreater.this, PdfViewerActivity.class);
//        intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);

//        startActivity(intentPdfViewer);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
//        if(fileWithinMyDir.exists()) {
        intentShareFile.setType("application/pdf");

        intentShareFile.putExtra(Intent.EXTRA_STREAM, pdfUri);

        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                "Sharing File...");
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

        startActivity(Intent.createChooser(intentShareFile, "Share File"));
//        }

    }
}
