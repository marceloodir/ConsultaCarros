package com.example.marcelo.pesquisacarro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import java.util.ArrayList;


public class Listagem extends ActionBarActivity {
    private User user = SerealizarUser.loadUser();
    private GridView grid = null;
    private ArrayList<Veiculo> veiculos;
    private VeiculosAdapter adapter;
    private Dialog dialog;
    private DBHelper mydb;

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
            Veiculo veiculo = new Veiculo(placa.getText().toString(),renavam.getText().toString());
            mydb.insertVeiculo(veiculo);
            veiculos.add(veiculo);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        }
    };

}
