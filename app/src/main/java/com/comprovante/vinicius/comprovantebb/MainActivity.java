package com.comprovante.vinicius.comprovantebb;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.PermissionRequest;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    public CalendarView start;
    public CalendarView end;
    public int sd, sm, sy, ed, em, ey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (CalendarView) this.findViewById(R.id.calendarView);
        end = (CalendarView) this.findViewById(R.id.calendarView2);


        sd = Integer.parseInt(new SimpleDateFormat("dd").format(new Date(start.getDate())));
        sm = Integer.parseInt(new SimpleDateFormat("MM").format(new Date(start.getDate())));
        sy = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date(start.getDate())));
        ed = Integer.parseInt(new SimpleDateFormat("dd").format(new Date(start.getDate())));
        em = Integer.parseInt(new SimpleDateFormat("MM").format(new Date(start.getDate())));
        ey = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date(start.getDate())));


        start.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                sd = dayOfMonth;
                sm = month + 1;
                sy = year;
            }
        });

        end.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                ed = dayOfMonth;
                em = month + 1;
                ey = year;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.barmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.generate:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    if (sy * 10000 + sm * 100 + sd <= ey * 10000 + em * 100 + ed) {

                        ReadFilesReceipt r = new ReadFilesReceipt(sd, sm, sy, ed, em, ey, this.getApplicationContext());
                        Toast.makeText(getApplicationContext(), "start: " + sd + "/" + sm + "/" + sy + "\n" + "end: " + ed + "/" + em + "/" + ey, Toast.LENGTH_LONG).show();


                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("application/pdf");

                        //Uri uri = Uri.parse("file://" + r.getFilePDF().getAbsolutePath()); //api 23 or lower

                        Uri uri = FileProvider.getUriForFile(MainActivity.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                r.getFilePDF());

                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "print PDF "+ sm +"/"+ sy);


                        try {
                            startActivity(Intent.createChooser(intent, "Send PDF"));
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: Cannot open or share created PDF report.", Toast.LENGTH_SHORT).show();
                        }

                    } else
                        Toast.makeText(getApplicationContext(), "start date < end date", Toast.LENGTH_LONG).show();
                }
                else
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                0);

                return true;

        }
        return false;
    }
}
