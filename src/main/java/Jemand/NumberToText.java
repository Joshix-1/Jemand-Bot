package Jemand;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Aufgabe: In Verträgen und Schecks muß man immer auch den Betrag in Worten schreiben. Für eine
 * Versicherung  sollen Sie ein Programm schreiben, das eine von stdin zeilenweise Zahlen einliest
 * und diese Zahlen gemein- sam mit den Worten, die diese Zahl repräsentieren, ausgeben. Für die
 * Zahl 21 soll also zum Beispiel der Text "einundzwanzig" ausgegeben werden. Ihr Programm soll
 * alle ganzen Zahlen von 0 bis  2147483647 verarbeiten können. Berücksichtigen dabei auch die
 * korrekte Schreibweise. Laut Duden 1973, S. 59 ff, schreibt man Zahlen unter einer Million
 * zusammen. Getrennt schreibt man dagegen Zahlen über einer Million.  Beispiele:
 * eintausendneunhundertfünfundfünfzig,  dreiundzwanzigtausend, zwei Millionen
 * dreitausendvierhundert  Lösung: Das Programm teilt die eingegebene Zahl in Blöcke zu drei
 * Ziffern, indem die Zahlen  solange durch 1000 dividiert wird, bis bei dieser Division 0
 * herauskommt. Jeder dieser Blöcke  wird durch Aufruf einer Funktion in das entsprechende Wort
 * gewandelt. Danach wird je nach  Wertigkeit des Blockes das Wort "tausend", "Million", usw.
 * angehängt.
 *
 * @author Peter René Dietmüller
 * @version 1.1
 */
public class NumberToText {
    /**
     * Gibt das Wort für die Zehnerstellen zurück.
     *
     * @param x die umzuwandelnde Zahl
     *
     * @return Text für die Zahl
     */
    private static String intToText10(int x) {
        int y=x/10; /* nur Zehnerstelle */
        switch(y) {
            case 1:
                return "zehn";
            case 2:
                return "zwanzig";
            case 3:
                return "dreißig";
            case 4:
                return "vierzig";
            case 5:
                return "fünfzig";
            case 6:
                return "sechzig";
            case 7:
                return "siebzig";
            case 8:
                return "achtzig";
            case 9:
                return "neunzig";
            default:
                return "FEHLER";
        }
    }

    /**
     * Gibt den Betrag in Worten für 1 bis 99 zurück.
     *
     * @param x die umzuwandelnde Zahl
     * @param digits Anzahl der Stellen hinter der Zahl
     *
     * @return Text für die Zahl
     */
    private static String intToText100(int x,int digits) {
        int y=x%100; /* nur Einer- und Zehnerstelle */
        /* -- Sonderbehandlung der Zahl 1 -- */
        if(y==1) {
            switch(digits) {
                case 0:
                    return "eins";
                case 1:
                case 2:
                case 3:
                    return "ein";
                case 6:
                case 9:
                case 12:
                case 15:
                    /* -- Einzahl oder Mehrzahl ? -- */
                    if(x==1) {
                        return "eine";
                    } else {
                        return "ein";
                    }
                default:
                    return "FEHLER";
            }
        }
        /* -- Zahlen 2 bis 19 -- */
        if((y>=2) && (y<=19)) {
            switch(y) {
                case 2:
                    return "zwei";
                case 3:
                    return "drei";
                case 4:
                    return "vier";
                case 5:
                    return "fünf";
                case 6:
                    return "sechs";
                case 7:
                    return "sieben";
                case 8:
                    return "acht";
                case 9:
                    return "neun";
                case 10:
                    return "zehn";
                case 11:
                    return "elf";
                case 12:
                    return "zwölf";
                case 13:
                    return "dreizehn";
                case 14:
                    return "vierzehn";
                case 15:
                    return "fünfzehn";
                case 16:
                    return "sechzehn";
                case 17:
                    return "siebzehn";
                case 18:
                    return "achtzehn";
                case 19:
                    return "neunzehn";
                default:
                    return "FEHLER";
            }
        }
        /* -- Zahlen 20 bis 99 -- */
        if(y>=20) {
            if(y%10==0) {
                return intToText10(y);
            } else {
                return intToText100(y%10,1)+"und"+intToText10(y);
            }
        }
        return "";
    }

    /**
     * Gibt den Betrag in Worten für 1 bis 999 zurück.
     *
     * @param x die umzuwandelnde Zahl
     * @param digits Anzahl der Stellen hinter der Zahl
     *
     * @return Text für die Zahl
     */
    private static String intToText1000(int x,int digits) {
        if(x/100==0) {
            return intToText100(x,digits);
        } else {
            return intToText100(x/100,2)+"hundert"+intToText100(x,digits);
        }
    }

    /**
     * Bezeichnung der Werte über einer Miilion
     *
     * @param digits Anzahl der Stellen
     * @param mz Merhzahl
     *
     * @return Zahlen-Suffix
     */
    private static String intToTextDigits(int digits,boolean mz) {
        if(mz) { /* Mehrzahl oder Einzahl ? */
            switch(digits) {
                case 0:
                    return "";
                case 3:
                    return "tausend";
                case 6:
                    return " Millionen ";
                case 9:
                    return " Milliarden ";
                case 12:
                    return " Billionen ";
                case 15:
                    return " Billiarden ";
                default:
                    return "";
            }
        } else {
            switch(digits) {
                case 0:
                    return "";
                case 3:
                    return "tausend";
                case 6:
                    return " Million ";
                case 9:
                    return " Milliarde ";
                case 12:
                    return " Billion ";
                case 15:
                    return " Billiarde ";
                default:
                    return "";
            }
        }
    }

    /**
     * Betrag in Worten einer ganzen, positiven Zahl
     *
     * @param x umzuwandelnde Zahl
     *
     * @return Zahl in Worten
     */
    public static String intToText(int x) {
        int digits; /* Anzahl der bearb. Stellen */
        String result; /* Zahl in Worten            */
        /* -- Sonderfall 0 -- */
        if(x==0) {
            return "null";
        }
        /* -- alle anderen ganzen Zahlen -- */
        digits=0;
        result=x<0?"minus ": "";
        x = Math.abs(x);
        while(x>0) {
            result=(x%1000>0?(intToText1000(x%1000,digits)+intToTextDigits(digits,x%1000>1)):"")+result;
            x/=1000;
            digits+=3;
        }
        return result;
    }
}