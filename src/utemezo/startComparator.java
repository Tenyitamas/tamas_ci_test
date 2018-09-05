package utemezo;

import java.util.Comparator;
/**
 *  Comparator, növekvő sorrendbe teszi a kezdési idő szerint, ha megegyezik, akkor érkezési sorrend szerint
 */
public class startComparator implements Comparator<Taszk> {

    @Override
    public int compare(Taszk o1, Taszk o2) {
        if(o1.getStart() < o2.getStart()){
            return -1;
        }else if (o1.getStart() > o2.getStart()) {
            return 1;
        }else if(o1.getId() < o2.getId()){
            return -1;
        }else if(o1.getId() > o2.getId()){
            return 1;
        }else{
            return 0; //elv ez nem kovetkezhet be... az idjuk unique
        }

    }
}
