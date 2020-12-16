package com.vimal.filedownloader.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vimal.filedownloader.R;
import com.vimal.filedownloader.models.FileItem;

import java.text.DecimalFormat;

public class DetailsActivity extends AppCompatActivity {

    Context mContext;
    ImageView backButton;
    FileItem selectedItem;
    TextView tvName, tvSize, tvLocation;
    int selectedSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mContext = this;
        findViewById();
        setOnClickListener();
        bindMethod();
    }

    public void findViewById() {
        backButton = findViewById(R.id.backButton);
        tvName = findViewById(R.id.tvName);
        tvSize = findViewById(R.id.tvSize);
        tvLocation = findViewById(R.id.tvLocation);
    }

    public void setOnClickListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void bindMethod() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            selectedItem = (FileItem) b.getSerializable("selectedItem");

            tvName.setText("File Name : " + b.getString("selectedName"));
            tvLocation.setText("Location : " + b.getString("selctedPath"));

            selectedSize = b.getInt("selectedSize");
            tvSize.setText("Size : " + getStringSizeLengthFile(selectedSize));
            System.out.println("VIMAL : name :" + b.getString("selectedName") + "");

        }
    }

    public static String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;

        if (size < sizeMb)
            return df.format(size / sizeKb) + " Kb";
        else if (size < sizeGb)
            return df.format(size / sizeMb) + " Mb";
        else if (size < sizeTerra)
            return df.format(size / sizeGb) + " Gb";

        return "";
    }
}