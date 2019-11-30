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
        get("/", (request, response) -> {
            StartUser();
            String user = request.cookie("LoginU");
            if (user != null) {
                String passw = Encryption.Decrypt(request.cookie("LoginP"));
                String usern = Encryption.Decrypt(user);
                currentUser = UserServices.getInstancia().getUser(usern, passw);
                CreateSession(request, currentUser);
            }
            currentUser = getSessionUsuario(request);
            if (currentUser == null)
                currentUser = CreateSession(request, null);

            Map<String, Object> attributes = validateUser();
            return new ModelAndView(attributes, "home.ftl");
        }, freeMarkerEngine);

        get("/LogIn/", (request, response) -> {
            Map<String, Object> attributes = validateUser();
            return new ModelAndView(attributes, "signin.ftl");
        }, freeMarkerEngine);

        post("/logInUser/", (request, response) -> {
            User user = UserServices.getInstancia().getUser(request.queryParams("username"), encryptPassword(request.queryParams("password")));
            System.out.println(request.queryParams("username") + ": " + encryptPassword(request.queryParams("password")));
            if (user != null) {
                CreateSession(request, user);
                boolean remember = Boolean.parseBoolean(request.queryParams("remember"));
                if (remember) {
                    response.cookie("/", "LoginU", Encryption.Encrypt(user.getUsername()), 604800, false);
                    response.cookie("/", "LoginP", Encryption.Encrypt(user.getPassword()), 604800, false);
                }
                response.redirect("/");
            } else {
                response.redirect("/LogIn/");
            }
            return null;
        }, freeMarkerEngine);

        get("/Register/", (request, response) -> {
            Map<String, Object> attributes = validateUser();
            return new ModelAndView(attributes, "register.ftl");
        }, freeMarkerEngine);

        post("/registerUser/", (request, response) -> {
            User u = new User(request.queryParams("username"),
                    request.queryParams("name"),
                    encryptPassword(request.queryParams("password")),
                    Boolean.parseBoolean(request.queryParams("admin")));
            UserServices.getInstancia().insert(u);
            CreateSession(request, u);
            response.redirect("/");
            return null;
        }, freeMarkerEngine);

        post("/generateUrl", (request, response) -> {
            currentUser = getSessionUsuario(request);
            Url fu = gson.fromJson(request.body(), Url.class);
            Url returned_val = generateURL(fu.getUrl());
            UrlServices.getInstancia().insert(returned_val);
            return null;
        }, JsonTransformer.json());

        get("/AllUrls/", (request, response) -> {
            Map<String, Object> attributes = validateUser();
            return new ModelAndView(attributes, "urlList.ftl");
        }, freeMarkerEngine);

        get("/StatsUrl/:id", (request, response) -> {
            Map<String, Object> attributes = validateUser();
            Url url = UrlServices.getInstancia().buscar(Long.parseLong(request.params("id")));
            attributes.put("clickNum", url.getVisits().size());
            attributes.put("urlId", request.params("id"));
            attributes.put("url", url.getUrl());
            return new ModelAndView(attributes, "statPage.ftl");
        }, freeMarkerEngine);

        private User CreateSession(Request request, User user) {
            Session session = request.session(true);
            if (user == null) {
                aux = UserServices.getInstancia().getUser(session.id());
                if (aux == null) {
                    user = new User(session.id(), false);
                    UserServices.getInstancia().insert(user);
                } else
                    user = aux;
            }
            session.attribute("usuario", user);
            return user;
        }
