package utemezo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Predicate;

public class Utemezo {

    private char utolsoTaszk = 0; //Utolsó taszk karaktere, ha a Taszkban megváltoztatjuk Stringre, itt is kell
    private int ido = 0; //Az idő
    private final int loketido = 2; //Ha itt átállítjuk a löketidőt, elvileg mindenhol működik rendesen
    private int currloket = loketido; //Beállítjuk a jelenlegi löketidőt a maximumra
    private boolean rendeznikell = true;
    /**
     *  Comparator, növekvő sorrendbe teszi a löketidő szerint, SJF-hez kell, ha a löketidejűk megegyezik, akkor start szerint, ha az is, akkor ID szerint
     */

     class loketComparator implements Comparator<Taszk>{

        @Override
        public int compare(Taszk o1, Taszk o2) {
            if(o1.getCpuLoket() < o2.getCpuLoket()){
                return -1;
            }else if (o1.getCpuLoket() > o2.getCpuLoket()) {
                return 1;
            }else if( o1.getCpuLoket() == o2.getCpuLoket() && o1.getStart() < o2.getStart()){
                return -1;
            } else if (o1.getCpuLoket() == o2.getCpuLoket() && o2.getStart() > o2.getStart()){
                return 1;
            }else if( o1.getId() < o2.getId()){
                return -1;
            } else if (o1.getId() > o2.getId()){
                return 1;
            } else{
                return 0; //ez elvileg lehetetlen
            }

        }
    }

    /**
     *  Predikátum, amennyiben a taszk végzett, naplózza a végének idejét
     */

    class finishedPredicate implements Predicate<Taszk>{

        @Override
        public boolean test(Taszk taszk) {
            if(taszk.getLoketMaradt() < 1){
                taszk.setEnd(ido);
                currloket = loketido; //resetelni kell, mert ha pont kitörlődik akkor rosszul swapol
                if(taszk.getPrioritas() == 1){
                    rendeznikell = true;
                }
                return true;
            }
            return false;
        }
    }




    /**
     * Rendezi az Arraylistet a predikátum szerint, csak a kód szépsége miatt létezik
     * @param in Arraylist a bemenethez, Taszkokat tartalmaz
     * @param c Comparator, hogy mi szerint rendezze
     */

    private void sortArrayList(ArrayList<Taszk> in, Comparator<Taszk> c ){
        Collections.sort(in, c);
    }

    /**
     * Meghatározza, hogy a Taszkok végeztek-e
     * @param in ArrayList, amiben meg kell számolni a taszkokat
     * @return true - minden taszk végzett, false - nem minden taszk végzett
     */

    private boolean countFinished(ArrayList<Taszk> in){
        int db = 0;

        for(Taszk t : in){
            if(t.getLoketMaradt() < 1){
                db++;
            }
        }

        return db == in.size();



    }

    /**
     * Az ütemező elindítása
     * @param taszkok A futtatandó taszkok ArrayListje
     * @return A futási sorrend Stringje
     */

    public String utemez(ArrayList<Taszk> taszkok){
        //Init
        ido = 0; //Ha többször szeretnénk futtatni, akkor célszerű
        String out = "";
        ArrayList<Taszk> overallBuffer = new ArrayList<>();
        ArrayList<Taszk> kernelBuffer = new ArrayList<>();
        LinkedList<Taszk> felhBuffer = new LinkedList<>();
        startComparator sC = new startComparator();
        loketComparator lC = new loketComparator();
        finishedPredicate fP = new finishedPredicate();


        //Prepare
        overallBuffer.addAll(taszkok); //Lemásoljuk egy új listába a taszkokat, shallow copy
        sortArrayList(overallBuffer, sC); //Rendezzük start idő szerint, közvetlen haszna nincs jelenleg, de optimalizáláshoz jó lenne

        while(!countFinished(taszkok)){
            //Bufferek ürítése, az elkészültek kitörlésével
            kernelBuffer.removeIf(fP);
            felhBuffer.removeIf(fP);

            ido++;


            //Bufferek töltése
            for(Taszk t : overallBuffer){
                if(t.getStart() < ido){ //Elosztani prioritások szerint
                    if(t.getPrioritas() == 0){
                        felhBuffer.offerLast(t);
                    }else if(t.getPrioritas() == 1){
                        kernelBuffer.add(t);
                    }else{
                        System.out.println("HIBA!");
                    }
                }
            }

            //Nagy buffer üritése
            overallBuffer.removeAll(kernelBuffer);
            overallBuffer.removeAll(felhBuffer);

            //Bufferek rendezése
            if(!kernelBuffer.isEmpty()){    //Van a kernelbufferben, az élvez prioritást

                if(!felhBuffer.isEmpty() && currloket != loketido){
                    felhBuffer.offerLast(felhBuffer.pollFirst()); //Visszadobni az éppen CPU-n lévőt
                    currloket = loketido;   //Resetelni a löketidőt
                }
                if(rendeznikell){
                    sortArrayList(kernelBuffer, lC); //Rendezni SJF
                    rendeznikell = false;
                }


                kernelBuffer.get(0).csokkentLoketMaradt(); //Első (shortest job)
                if(utolsoTaszk == 0 || utolsoTaszk != kernelBuffer.get(0).getNev()){ //Ha üres vagy nem ugyanaz
                    utolsoTaszk = kernelBuffer.get(0).getNev(); //Beállít a nevét
                    out += utolsoTaszk; //Concat az outputhoz
                }


            }else if(!felhBuffer.isEmpty()){ //Nincs kernelben, akkor van felhasználóiban,


                if(currloket < 1){ //Ha letelt a löketidő
                    currloket = loketido; //Reset löketidő
                    felhBuffer.offerLast(felhBuffer.pollFirst()); //vissza a végére
                }

                felhBuffer.getFirst().csokkentLoketMaradt(); //csökkenteni a hátralévő idejét az első taszknak
                if(utolsoTaszk == 0 || utolsoTaszk != felhBuffer.getFirst().getNev()){ //ha üres vagy nem ugyanaz
                    utolsoTaszk = felhBuffer.getFirst().getNev(); //beállít
                    out += utolsoTaszk; //concat
                }
                currloket--; //csökkenteni az elérhető löketidőt

            }

           // System.out.println(ido + " " + utolsoTaszk );

        }
        felhBuffer.removeIf(fP); //az utolsót is kivenni, és nyugtázni az idejét
        kernelBuffer.removeIf(fP); //az utolsót is kivenni, és nyugtázni az idejét



        return out; //visszatérni a sorrenddel, a többi infó már a taszkokban van
    }









}
