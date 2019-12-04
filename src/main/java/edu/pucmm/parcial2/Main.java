package edu.pucmm.parcial2;

import edu.pucmm.parcial2.Rutas.*;
import edu.pucmm.parcial2.Servicios.StartDatabase;

public class Main {
    public static void main(String[] args) throws Exception {
        StartDatabase.getInstancia().startDb();
        new mainHandler().startup();



    }
}
