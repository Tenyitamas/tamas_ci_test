package utemezo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {

        //Init
        Utemezo u = new Utemezo();
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String temp = "";
        ArrayList<Taszk> taszkok = new ArrayList<>();


        //Process
        do{
            try {
                temp = bf.readLine();
                if(temp.equals("")){
                    break;
                }
                String[] data = temp.split(",");
                taszkok.add(new Taszk(data[0].charAt(0), Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]) ) ); //beolvas, elszáll ha hibás formátum

            } catch (IOException e) {
                e.printStackTrace();
            }

        }while(!temp.equals(""));

        System.out.println(u.utemez(taszkok));

        Collections.sort(taszkok, new startComparator());

        for(int i = 0; i < taszkok.size(); i++){
            System.out.print(taszkok.get(i).getNev() + ":" + taszkok.get(i).getVarakozas() + (i < taszkok.size()-1 ? "," : "") ); //kiiratni az adatokat, az utolsónál nem kell vessző
        }


    }
}
