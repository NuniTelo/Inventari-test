package com.inventar.nuni.inventari;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.inventar.nuni.inventari.info.ArtikujDatabaze;
import com.inventar.nuni.inventari.info.DownloadShitje;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class BackgroundService extends Service {
    private List<String> id_db,emri_db,njesi_db,kategori_db,cmim_db,data_db;
    private ArrayList<String> id;
    private ArrayList<String>njesi,sasi,data;
    private String url_kerkuar = "https://dl.dropboxusercontent.com/s/q6hhhxrbcw4u02y/artikulli.txt?dl=0";
    DatabazeCon mydb = new DatabazeCon(this);
    ArtikujDatabaze mydb_shitje = new ArtikujDatabaze(this);
    private String url_shitje= "https://dl.dropboxusercontent.com/s/q8j6mdrsnx51gcm/artikulli.txt?dl=0";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        final CountDownTimer rifreskim = new CountDownTimer((long) 10000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                download_artikuj();
                download_artikuj_info();

                this.start();
            }
        }.start();


        return super.onStartCommand(intent,flags,startId);
    }

    public void download_artikuj(){
        DownloadTask task = new DownloadTask();
        String res = null;
        try {
            res = task.execute(url_kerkuar).get();
            String[] records = res.split(";");
            id_db = new ArrayList<>();
            emri_db = new ArrayList<>();
            njesi_db = new ArrayList<>();
            kategori_db = new ArrayList<>();
            cmim_db = new ArrayList<>();
            data_db = new ArrayList<>();

            for (String record : records) {
                String[] recordData = record.split(",");
                id_db.add(recordData[0]);
                emri_db.add(recordData[1]);
                njesi_db.add(recordData[2]);
                kategori_db.add(recordData[3]);
                cmim_db.add(recordData[4]);
                data_db.add(recordData[5]);
            }
            //mydb.fshi();
            for (int i = 0; i < emri_db.size(); i++) {
                mydb.shto_artikull(id_db.get(i), emri_db.get(i), njesi_db.get(i), kategori_db.get(i), cmim_db.get(i), data_db.get(i));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void download_artikuj_info(){
        DownloadShitje task = new DownloadShitje();
        String res = null;
        id = new ArrayList<>();
        njesi = new ArrayList<>();
        sasi = new ArrayList<>();
        data = new ArrayList<>();
        try {
            res = task.execute(url_kerkuar).get();
            String[] records = res.split(";");

            for (String record : records) {
                String[] recordData = record.split(",");
                id.add(recordData[0]);
                njesi.add(recordData[1]);
                sasi.add(recordData[2]);
                data.add(recordData[3]);
            }
            //mydb.fshi();
            for (int i = 0; i < id.size(); i++) {
                mydb_shitje.shto_artikuj_shitje(id.get(i), njesi.get(i), sasi.get(i), data.get(i));

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
