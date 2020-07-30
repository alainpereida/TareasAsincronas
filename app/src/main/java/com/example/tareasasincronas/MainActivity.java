package com.example.tareasasincronas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnSinHilos, btnHilo, btnAsyncTask, btnCancelar, btnAsyncDialog;
    private ProgressBar pbarProgreso;
    private ProgressDialog progressDialog;

    private MiTareaAsincrona tarea1;
    private MiTareaAsincronaDialog tarea2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSinHilos = findViewById(R.id.btnSinHilos);
        btnHilo = findViewById(R.id.btnHilo);
        btnAsyncTask = findViewById(R.id.btnAsyncTask);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnAsyncDialog = findViewById(R.id.btnAsyncDialog);
        pbarProgreso = findViewById(R.id.pbarProgreso);

        btnSinHilos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbarProgreso.setMax(100);
                pbarProgreso.setProgress(0);
                for (int i = 1; i <= 10; i++) {
                    tareaLarga();
                    pbarProgreso.incrementProgressBy(10);
                }
                Toast.makeText(MainActivity.this, "Tarea Finalizada", Toast.LENGTH_LONG).show();
            }
        });

        btnHilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pbarProgreso.post(new Runnable() {
                            @Override
                            public void run() {
                                pbarProgreso.setProgress(0);
                            }
                        });

                        for (int i = 0; i <= 10; i++) {
                            tareaLarga();
                            pbarProgreso.post(new Runnable() {
                                @Override
                                public void run() {
                                    pbarProgreso.incrementProgressBy(10);
                                }
                            });
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).start();
            }
        });

        btnAsyncTask.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tarea1 = new MiTareaAsincrona();
                tarea1.execute();
            }

        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tarea1.cancel(true);
            }
        });

        btnAsyncDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("Procesando...");
                progressDialog.setCancelable(true);
                progressDialog.setMax(100);

                tarea2 = new MiTareaAsincronaDialog();
                tarea2.execute();
            }
        });

    }

    private void tareaLarga() {
        try {
            Thread.sleep(1000);
        }catch (InterruptedException  e) {

        }
    }

    private class MiTareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            for(int i = 1; i <= 10; i++) {
                tareaLarga();

                publishProgress(i*10);

                if(isCancelled())
                    break;
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();

            pbarProgreso.setProgress(progreso);
        }

        @Override
        protected void onPreExecute() {
            pbarProgreso.setMax(100);
            pbarProgreso.setProgress(0);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
                Toast.makeText(MainActivity.this, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(MainActivity.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
        }
    }

    private class MiTareaAsincronaDialog extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            for(int i = 1; i <= 10; i++) {
                tareaLarga();

                publishProgress(i*10);

                if(isCancelled())
                    break;
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();

            progressDialog.setProgress(progreso);
        }

        @Override
        protected void onPreExecute() {

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    MiTareaAsincronaDialog.this.cancel(true);
                }
            });

            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(MainActivity.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
        }
    }
}
