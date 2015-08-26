package com.example.marcelo.pesquisacarro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Listagem extends ActionBarActivity {
    private User user = SerealizarUser.loadUser();
    private GridView grid = null;
    private ArrayList<Veiculo> veiculos;
    private VeiculosAdapter adapter;
    private Dialog dialog;
    private DBHelper mydb;
    private String URL = "http://192.168.1.107/servico/consultas.asmx/consultas";
    private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem);
        grid = (GridView) findViewById(R.id.gridView);
        mydb = new DBHelper(this);
        veiculos = mydb.getAllVeiculos();
        adapter = new VeiculosAdapter(this,0,veiculos);
        grid.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listagem, menu);
        MenuItem item = menu.findItem(R.id.user);
        item.setTitle(user.getUsuario());
        return true;
    }


    public void logout(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sair);
        builder.setMessage(R.string.removeuser);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SerealizarUser.removeUser();
                mydb.removeDB(Listagem.this);
                Intent intent = new Intent(Listagem.this, MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void sobre(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sobre);
        builder.setMessage(R.string.sobreconteudo);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void adicionar(View view) {
        dialog =  new Dialog(this);
        dialog.setTitle(R.string.nveiculo);
        dialog.setContentView(R.layout.cadastro_veiculo);
        Button cadastrarBT = (Button) dialog.findViewById(R.id.veiculoBT);
        cadastrarBT.setOnClickListener(cadastroVeiculo);
        dialog.show();
    }

    public void consultar(View view) {
        Toast.makeText(Listagem.this, R.string.atualizando, Toast.LENGTH_LONG).show();
        ArrayList<Veiculo> veiculosCheck = queryWS2(veiculos);
        updateVeiculos(veiculosCheck);
    }

    public void removerVeiculo(View v) {
        final TextView placa = (TextView) v.findViewById(R.id.placaTV);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.apagarveiculo) + placa.getText().toString());
        builder.setMessage(R.string.apagarmessagem);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (Veiculo cada : veiculos) {
                    if (cada.getPlaca().equals(placa.getText().toString())) {
                        mydb.deleteVeiculo(cada);
                        veiculos.remove(cada);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private View.OnClickListener cadastroVeiculo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText placa = (EditText) dialog.findViewById(R.id.placaCad);
            EditText renavam = (EditText) dialog.findViewById(R.id.renavamCad);
            Veiculo veiculo = new Veiculo(placa.getText().toString().toUpperCase(),renavam.getText().toString().toUpperCase());
            mydb.insertVeiculo(veiculo);
            veiculos.add(veiculo);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        }
    };

    private  void queryWS(final ArrayList<Veiculo> veiculos) {
        final ArrayList<Veiculo> retorno;
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;

                try {
                    HttpPost post = new HttpPost(URL);

                    Gson gson =  new Gson();
                    JsonElement element = gson.toJsonTree(veiculos, new TypeToken<ArrayList<Veiculo>>() {
                    }.getType());
                    JsonArray jsonArray = element.getAsJsonArray();

                    ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>(1);
                    parameters.add(new BasicNameValuePair("entrada",jsonArray.toString()));
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
                    post.setEntity(entity);

                    response = client.execute(post);

                    /*Checking response */
                    if(response.getStatusLine().getStatusCode() == 200){
                        InputStream content = response.getEntity().getContent();
                        Reader reader = new InputStreamReader(content);
                        gson = new Gson();
                        ArrayList<Veiculo> retorno = gson.fromJson(reader, ArrayList.class);
                        updateVeiculos(retorno);
                        content.close();
                        latch.countDown();
                    }else{
                        Toast.makeText(Listagem.this, R.string.errorcomunicacao, Toast.LENGTH_LONG).show();
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    Log.v("erroRetorno", e.toString());
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Veiculo> queryWS2(final ArrayList<Veiculo> veiculos) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<ArrayList<Veiculo>> callable = new Callable<ArrayList<Veiculo>>() {
            @Override
            public ArrayList<Veiculo> call() throws Exception {
                ArrayList<Veiculo> retorno = null;
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                HttpPost post = new HttpPost(URL);

                Gson gson =  new Gson();
                JsonElement element = gson.toJsonTree(veiculos, new TypeToken<ArrayList<Veiculo>>() {
                }.getType());
                JsonArray jsonArray = element.getAsJsonArray();

                ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>(1);
                parameters.add(new BasicNameValuePair("entrada",jsonArray.toString()));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
                post.setEntity(entity);

                response = client.execute(post);

                if(response.getStatusLine().getStatusCode() == 200){
                    InputStream content = response.getEntity().getContent();
                    Reader reader = new InputStreamReader(content);
                    gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Veiculo>>(){}.getType();
                    retorno = gson.fromJson(reader, listType);
                    content.close();
                }else{
                    Toast.makeText(Listagem.this, R.string.errorcomunicacao, Toast.LENGTH_LONG).show();
                }
                return retorno;
            }
        };
        Future<ArrayList<Veiculo>> retorno = executor.submit(callable);
        executor.shutdown();
        try {
            return retorno.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateVeiculos(ArrayList<Veiculo> veiculos) {
        try {
            this.veiculos = veiculos;

            adapter = new VeiculosAdapter(this,0,this.veiculos);
            grid.setAdapter(adapter);

            mydb.removeAllVeiculos();
            for (Veiculo v : veiculos) {
                mydb.insertVeiculo(v);
            }
        }catch (Exception e){
            Log.v("insertError",e.toString());
        }
        Toast.makeText(Listagem.this, R.string.atualizado, Toast.LENGTH_LONG).show();
    }

}
