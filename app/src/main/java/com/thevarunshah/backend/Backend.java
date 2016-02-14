package com.thevarunshah.backend;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Backend {

    public static String baseURL = "http://2112d6b6.ngrok.com/api";
    public static String token = null;

    public static void backupToken(Context context){

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput("token.ser", Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(token);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try{
                oos.close();
                fos.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void readToken(Context context){

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput("token.ser");
            ois = new ObjectInputStream(fis);
            token = (String)ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try{
                if(ois != null) ois.close();
                if(fis != null) fis.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
