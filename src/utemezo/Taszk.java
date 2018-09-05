package utemezo;

public class Taszk {

    private int prioritas;  //prioritás, kernek = 1, felh = 0
    private char nev; //taszk neve, betűjel, szóval karakter, bármikor változtatható minimális módosítással
    private int start; //indítási ideje
    private int cpuLoket; //löketidő
    private int end; //mikor végzett
    private int loketMaradt; //mennyi futási idő szükséges még
    private int id; //taszk id, sorrend miatt fontos
    private static int taskid = 0; //statikus, minden taszk új id-t kap

    public Taszk(char nev, int prioritas, int start, int cpuLoket) {
        this.prioritas = prioritas;
        this.nev = nev;
        this.start = start;
        this.cpuLoket = cpuLoket;
        this.loketMaradt = cpuLoket;
        id = taskid++;

    }

    public int getId() {
        return id;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getLoketMaradt() {
        return loketMaradt;
    }

    public void csokkentLoketMaradt(){
        loketMaradt--;
    }

    public void setLoketMaradt(int loketMaradt) {
        this.loketMaradt = loketMaradt;
    }

    public int getPrioritas() {
        return prioritas;
    }

    public char getNev() {
        return nev;
    }

    public int getStart() {
        return start;
    }

    public int getCpuLoket() {
        return cpuLoket;
    }

    public int getVarakozas(){
        return end-start-cpuLoket;
    }

}
