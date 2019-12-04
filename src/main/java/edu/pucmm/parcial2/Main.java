package edu.pucmm.parcial2;

import edu.pucmm.parcial2.Rutas.*;
import edu.pucmm.parcial2.Servicios.StartDatabase;
import freemarker.template.Configuration;
import spark.template.freemarker.FreeMarkerEngine;

import static spark.Spark.port;
import static spark.Spark.staticFiles;

public class Main {
    public static void main(String[] args) throws Exception {

        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            port(Integer.parseInt(processBuilder.environment().get("PORT")));
        }else{
            port(8080);
        }
        StartDatabase.getInstancia().startDb();

        staticFiles.location("/publico");

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setClassForTemplateLoading(mainHandler.class, "/templates");
        FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(configuration);

        new mainHandler().startup();



    }
}
