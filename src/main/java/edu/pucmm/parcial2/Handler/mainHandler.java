package edu.pucmm.parcial2.Handler;

import com.google.gson.Gson;
import edu.pucmm.parcial2.Encapsulation.Groupby;
import edu.pucmm.parcial2.Encapsulation.Url;
import edu.pucmm.parcial2.Encapsulation.User;
import edu.pucmm.parcial2.Encapsulation.Visit;
import edu.pucmm.parcial2.Services.Encryption;
import edu.pucmm.parcial2.Services.UrlServices;
import edu.pucmm.parcial2.Services.UserServices;
import edu.pucmm.parcial2.Services.VisitServices;
import edu.pucmm.parcial2.Transformation.JsonTransformer;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Session;
import spark.template.freemarker.FreeMarkerEngine;

import java.net.InetAddress;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

import static spark.Spark.*;

public class mainHandler {
    public mainHandler() {
    }

    static User currentUser = new User();
    Gson gson = new Gson();
    //    static String ip_val, hex_val, new_url, base_url = "https://www.geekping.studio/ne/";
    static String ip_val, hex_val, new_url, base_url = "http://localhost:4567/ne/";

    static Url url_value;
    User aux;
    String so, browser, auxS;
    int auxi, auxi2;

    public void startup() {
        staticFiles.location("/publico");

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setClassForTemplateLoading(mainHandler.class, "/templates");
        FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(configuration);

        before("/ne/*", (request, response) -> {
            String redirect = base_url + request.uri().split("/")[2];
            Url ret_val = UrlServices.getInstancia().getUrl(redirect);
            if (ret_val == null)
                response.redirect("/");
            else {
                so = getSo(request.userAgent().toLowerCase());
                browser = getBrowser(request.userAgent().toLowerCase());
                auxS = request.ip();
                Visit v = new Visit(browser, so, new Date(), auxS, ret_val);
                VisitServices.getInstancia().insert(v);
                response.redirect(ret_val.getUrl());
            }
        });