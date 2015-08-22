package com.example.marcelo.pesquisacarro;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by marcelo on 22/08/15.
 */
public class VeiculosAdapter extends ArrayAdapter<Veiculo> {
    public VeiculosAdapter(Context context, int resource, ArrayList<Veiculo> veiculos) {
        super(context, resource, veiculos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Veiculo veiculo = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listagem_veiculos,parent,false);
        }

        TextView placa = (TextView) convertView.findViewById(R.id.placaTV);
        TextView renavam = (TextView) convertView.findViewById(R.id.renavamTV);
        TextView check = (TextView) convertView.findViewById(R.id.checkTV);

        placa.setText(veiculo.getPlaca());
        renavam.setText(veiculo.getRenavam());
        check.setText(veiculo.getStatus());

        return  convertView;
    }
}
