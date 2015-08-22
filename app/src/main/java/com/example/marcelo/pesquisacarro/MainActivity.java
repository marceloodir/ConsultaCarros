package com.example.marcelo.pesquisacarro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class MainActivity extends ActionBarActivity {
    public User user = null;
    private File file = null;
    private EditText usuarioET, senhaET;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = new File(getFilesDir(),"pesquisa.dat");
        usuarioET = (EditText) findViewById(R.id.emailET);
        senhaET = (EditText) findViewById(R.id.senhaET);

        this.user = Serealizar.loadUser(file);
        if (user != null && user.getIslogin()) {
            Intent intent = new Intent(this, Listagem.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    public void logar(View view) {
        user = Serealizar.loadUser(file);
        if (user == null) {
            Toast.makeText(MainActivity.this, R.string.cadastrarprimeiro, Toast.LENGTH_SHORT).show();
        }else{
            User usuarioInformado = new User(usuarioET.getText().toString(),senhaET.getText().toString());
            if(user.getUsuario().equals(usuarioInformado.getUsuario()) && user.getSenha().equals(usuarioInformado.getSenha()) ){
                user.setIslogin(true);
                Serealizar.saveUser(user,file);
                Intent intent = new Intent(this, Listagem.class);
                startActivity(intent);
            }else{
                Toast.makeText(MainActivity.this, R.string.usuariosenhaerrado, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cadastrar(MenuItem item) {
        dialog =  new Dialog(this);
        dialog.setTitle(R.string.nusuario);
        dialog.setContentView(R.layout.cadastrar);
        Button cadastrarBT = (Button) dialog.findViewById(R.id.cadastrar);
        cadastrarBT.setOnClickListener(criarCadastro);
        dialog.show();
    }

    private View.OnClickListener criarCadastro = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText emailCad = (EditText) dialog.findViewById(R.id.emailCad);
            EditText senhaCad = (EditText) dialog.findViewById(R.id.senhaCad);

            if(!isEmailValid(emailCad.getText())) {
                emailCad.setError("Email Inválido");
            }else{
                User user = new User(emailCad.getText().toString(),senhaCad.getText().toString());
                Serealizar.saveUser(user,file);
                dialog.dismiss();
                Toast.makeText(MainActivity.this, R.string.cadastradosucesso, Toast.LENGTH_SHORT).show();
            }
        }
    };

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
