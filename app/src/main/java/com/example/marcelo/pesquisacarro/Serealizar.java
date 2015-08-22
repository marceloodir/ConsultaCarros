package com.example.marcelo.pesquisacarro;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by marcelo on 22/08/15.
 */
public  class Serealizar {

    private static File file;

    public static void saveUser(User user) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(user);
            out.close();
        }catch (Exception e){
            Log.i("saveUser",e.toString());
            e.printStackTrace();
        }
    }

    public static User loadUser() {
        User user = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fis);
            user = (User) in.readObject();
            in.close();
        }catch (Exception e){
            Log.i("loadUser",e.toString());
            e.printStackTrace();
        }
        return user;
    }

    public static void removeUser() {
        file.delete();
    }

    public static File getFile() {
        return file;
    }

    public static void setFile(File file) {
        Serealizar.file = file;
    }
}
