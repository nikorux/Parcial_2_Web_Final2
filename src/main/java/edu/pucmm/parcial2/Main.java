package edu.pucmm.parcial2;

import edu.pucmm.parcial2.Handler.*;
import edu.pucmm.parcial2.Services.StartDatabase;

public class Main {
    public static void main(String[] args) throws Exception {
        StartDatabase.getInstancia().startDb();

    }
}
