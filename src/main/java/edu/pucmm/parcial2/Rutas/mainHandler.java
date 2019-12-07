package edu.pucmm.parcial2.Rutas;

import com.google.gson.Gson;
import edu.pucmm.parcial2.Encapsulaciones.Groupby;
import edu.pucmm.parcial2.Encapsulaciones.Url;
import edu.pucmm.parcial2.Encapsulaciones.Usuario;
import edu.pucmm.parcial2.Encapsulaciones.Visita;
import edu.pucmm.parcial2.Servicios.Encryption;
import edu.pucmm.parcial2.Servicios.UrlServices;
import edu.pucmm.parcial2.Servicios.UserServices;
import edu.pucmm.parcial2.Servicios.VisitServices;
import edu.pucmm.parcial2.JsonT.JsonTransformer;
import freemarker.template.Configuration;
import freemarker.template.Version;
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

    static Usuario currentUsuario = new Usuario();
    Gson gson = new Gson();
    static String ip_val, hex_val, new_url, base_url = "/ne/";

    static Url url_value;
    Usuario aux;
    String so, browser, auxS;
    int auxi, auxi2;

    public void startup() {


        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            port(Integer.parseInt(processBuilder.environment().get("PORT")));
        }else{
            port(8080);
        }


        staticFiles.location("/publico");

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setClassForTemplateLoading(mainHandler.class, "/templates");
        FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(configuration);

        before("/", (request, response) -> {
            String redirect = base_url + request.uri();
            Url ret_val = UrlServices.getInstancia().getUrl(redirect);
            if (ret_val == null)
                response.redirect("/");
            else {
                so = getSo(request.userAgent().toLowerCase());
                browser = getBrowser(request.userAgent().toLowerCase());
                auxS = request.ip();
                Visita v = new Visita(browser, so, new Date(), auxS, ret_val);
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
                currentUsuario = UserServices.getInstancia().getUser(usern, passw);
                CreateSession(request, currentUsuario);
            }
            currentUsuario = getSessionUsuario(request);
            if (currentUsuario == null)
                currentUsuario = CreateSession(request, null);

            Map<String, Object> attributes = validateUser();
            return new ModelAndView(attributes, "home.ftl");
        }, freeMarkerEngine);

        get("/LogIn/", (request, response) -> {
            Map<String, Object> attributes = validateUser();
            return new ModelAndView(attributes, "InicioS.ftl");
        }, freeMarkerEngine);

        post("/logInUser/", (request, response) -> {
            Usuario usuario = UserServices.getInstancia().getUser(request.queryParams("username"), encryptPassword(request.queryParams("password")));
            System.out.println(request.queryParams("username") + ": " + encryptPassword(request.queryParams("password")));
            if (usuario != null) {
                CreateSession(request, usuario);
                boolean remember = Boolean.parseBoolean(request.queryParams("remember"));
                if (remember) {
                    response.cookie("/", "LoginU", Encryption.Encrypt(usuario.getUsername()), 604800, false);
                    response.cookie("/", "LoginP", Encryption.Encrypt(usuario.getPassword()), 604800, false);
                }
                response.redirect("/");
            } else {
                response.redirect("/LogIn/");
            }
            return null;
        }, freeMarkerEngine);

        get("/Register/", (request, response) -> {
            Map<String, Object> attributes = validateUser();
            return new ModelAndView(attributes, "Registro.ftl");
        }, freeMarkerEngine);

        post("/registerUser/", (request, response) -> {
            Usuario u = new Usuario(request.queryParams("username"),
                    request.queryParams("name"),
                    encryptPassword(request.queryParams("password")),
                    Boolean.parseBoolean(request.queryParams("admin")));
            UserServices.getInstancia().insert(u);
            CreateSession(request, u);
            response.redirect("/");
            return null;
        }, freeMarkerEngine);

        post("/generateUrl", (request, response) -> {
            currentUsuario = getSessionUsuario(request);
            Url fu = gson.fromJson(request.body(), Url.class);
            Url returned_val = generateURL(fu.getUrl());
            UrlServices.getInstancia().insert(returned_val);
            return null;
        }, JsonTransformer.json());

        get("/AllUrls/", (request, response) -> {
            Map<String, Object> attributes = validateUser();
            return new ModelAndView(attributes, "ListaUrl.ftl");
        }, freeMarkerEngine);

        get("/StatsUrl/:id", (request, response) -> {
            Map<String, Object> attributes = validateUser();
            Url url = UrlServices.getInstancia().buscar(Long.parseLong(request.params("id")));
            attributes.put("clickNum", url.getVisits().size());
            attributes.put("urlId", request.params("id"));
            attributes.put("url", url.getUrl());
            return new ModelAndView(attributes, "PaginaEstadisticas.ftl");
        }, freeMarkerEngine);


        get("/LogOut/", (request, response) -> {
            request.session().removeAttribute("usuario");
            request.session().invalidate();
            currentUsuario = null;
            response.removeCookie("/", "LoginU");
            response.removeCookie("/", "LoginP");
            response.redirect("/");
            return null;
        }, freeMarkerEngine);

//        REST REQUESTS
        get("/rest/urlUser", (request, response) -> {
            currentUsuario = getSessionUsuario(request);
            if (currentUsuario != null) {
                Set<Url> vals = UserServices.getInstancia().buscar(currentUsuario.getId()).getUrls();
                response.header("Content-Type", "application/json");
                for (Url aux :
                        vals) {
                    aux.setUsuario(null);
                    aux.setVisits(null);
                }
                return vals;
            }
            response.status(404);
            System.out.println("ERROR");
            return null;
        }, JsonTransformer.json());

        get("/rest/urls", (request, response) -> {
            List<Url> vals = UrlServices.getInstancia().buscarTodos();
            response.header("Content-Type", "application/json");
            for (Url aux :
                    vals) {
                aux.setUsuario(null);
                aux.setVisits(null);
            }
            if (vals.size() == 0)
                response.status(404);

            return vals;
        }, JsonTransformer.json());

        get("/deleteUrl/:id", (request, response) -> {
            Url url = UrlServices.getInstancia().buscar(Long.parseLong(request.params("id")));
            for (Visita v :
                    url.getVisits()) {
                VisitServices.getInstancia().eliminar(v.getId());
            }
            UrlServices.getInstancia().eliminar(url.getId());
            response.redirect("/AllUrls/");
            return null;
        });
        get("/rest/browserUrl/:id", (request, response) -> {
            List<Visita> visits = VisitServices.getInstancia().getVisitbyUrl(Long.parseLong(request.params("id")));
            response.header("Content-Type", "application/json");
            return groupbyBrowser(visits);
        }, JsonTransformer.json());

        get("/rest/osUrl/:id", (request, response) -> {
            List<Visita> visits = VisitServices.getInstancia().getVisitbyUrl(Long.parseLong(request.params("id")));
            response.header("Content-Type", "application/json");
            return groupbySo(visits);
        }, JsonTransformer.json());

        get("/rest/dateUrl/:id", (request, response) -> {
            List<Visita> visits = VisitServices.getInstancia().getVisitbyUrl(Long.parseLong(request.params("id")));
            response.header("Content-Type", "application/json");
            return groupbyDate(visits);
        }, JsonTransformer.json());

        get("/rest/ipUrl/:id", (request, response) -> {
            List<Visita> visits = VisitServices.getInstancia().getVisitbyUrl(Long.parseLong(request.params("id")));
            response.header("Content-Type", "application/json");
            return groupbyIP(visits);
        }, JsonTransformer.json());

        // HttpResponse<String> response = Unirest.get('https://api.microlink.io?url=https%3A%2F%2Ftwitter.com%2Ffuturism%2Fstatus%2F882987478541533189');

    }

    public static Url generateURL(String a) throws Exception {
        if (a.isEmpty())
            return null;

        URL direction = new URL(a);
        direction.toURI();
        InetAddress ip = InetAddress.getByName(direction.getHost());
        ip_val = ip.getHostAddress();
        ip_val = ip_val.replace(".", "");
        ip_val = (Long.parseLong(ip_val) + LocalDateTime.now().getMinute() + LocalDateTime.now().getSecond()) + "";
        hex_val = longToHex(ip_val);
        new_url = base_url + hex_val;
        return new Url(a, ip.getHostAddress(), new_url, currentUsuario);
    }

    private static String longToHex(String value) {
        return Long.toHexString(Long.parseLong(value));
    }

    private Map<String, Object> validateUser() {
        Map<String, Object> attributes = new HashMap<>();
        if (currentUsuario == null || currentUsuario.getPassword() == null) {
            attributes.put("usuario", "other");
            attributes.put("userSigned", false);
            return attributes;
        } else {
            if (currentUsuario.isAdmin()) {
                attributes.put("usuario", "admin");
            } else {
                attributes.put("usuario", "visitor");
            }
            attributes.put("userSigned", true);
            return attributes;
        }
    }

    public static String getHash(String txt, String hashType) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance(hashType);
            byte[] array = md.digest(txt.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String encryptPassword(String txt) {
        return getHash(txt, "MD5");
    }

    private void StartUser() {
        if (UserServices.getInstancia().getUser("admin", encryptPassword("admin")) == null) {
            Usuario u= new Usuario("admin",
                    "admin",
                    encryptPassword("admin"),
                    true);
            UserServices.getInstancia().insert(u);
            System.out.println("ADMIN CREATED");
        }
    }

    private Usuario CreateSession(Request request, Usuario usuario) {
        Session session = request.session(true);
        if (usuario == null) {
            aux = UserServices.getInstancia().getUser(session.id());
            if (aux == null) {
                usuario = new Usuario(session.id(), false);
                UserServices.getInstancia().insert(usuario);
            } else
                usuario = aux;
        }
        session.attribute("usuario", usuario);
        return usuario;
    }

    private Usuario getSessionUsuario(Request request) {
        Usuario usuario = request.session().attribute("usuario");
        //System.out.println(user.getUsername() + " " + user.getId());
        return usuario;
    }

    private String getSo(String userAgent) {
        String so;
        if (userAgent.contains("android")) {
            so = "Android";
        } else if (userAgent.contains("iphone")) {
            so = "iPhone";
        } else if (userAgent.contains("windows")) {
            so = "Windows";
        } else if (userAgent.contains("macintosh")) {
            so = "Macintosh";
        } else if (userAgent.contains("linux")) {
            so = "Linux";
        } else {
            so = "UnKnown";
        }

        return so;
    }

    private String getBrowser(String userAgent) {
        String browser;
        if (userAgent.contains("ie") || userAgent.contains("rv")) {
            browser = "IE";
        } else if (userAgent.contains("opr") || userAgent.contains("opera")) {
            browser = "Opera";
        } else if (userAgent.contains("chrome")) {
            browser = "Chrome";
        } else if (userAgent.contains("firefox")) {
            browser = "Firefox";
        } else if (userAgent.contains("safari")) {
            browser = "Safari";
        } else {
            browser = "Other";
        }
        return browser;
    }

    private List<Groupby> groupbyBrowser(List<Visita> visits) {
        List<Groupby> chart_val = new ArrayList<>();
        for (Visita visit :
                visits) {
            auxi = find(chart_val, visit.getBrowser());
            if (auxi != -1) {
                auxi2 = chart_val.get(auxi).getValue();
                chart_val.get(auxi).setValue(auxi2 + 1);
            } else {
                chart_val.add(new Groupby(visit.getBrowser(), 1));
            }
        }
        return chart_val;
    }

    private List<Groupby> groupbySo(List<Visita> visits) {
        List<Groupby> chart_val = new ArrayList<>();
        for (Visita visit :
                visits) {
            auxi = find(chart_val, visit.getSo());
            if (auxi != -1) {
                auxi2 = chart_val.get(auxi).getValue();
                chart_val.get(auxi).setValue(auxi2 + 1);
            } else {
                chart_val.add(new Groupby(visit.getSo(), 1));
            }
        }
        return chart_val;
    }

    private List<Groupby> groupbyDate(List<Visita> visits) {
        List<Groupby> chart_val = new ArrayList<>();
        for (Visita visit :
                visits) {
            auxi = find(chart_val, visit.getFechaS());
            if (auxi != -1) {
                auxi2 = chart_val.get(auxi).getValue();
                chart_val.get(auxi).setValue(auxi2 + 1);
            } else {
                chart_val.add(new Groupby(visit.getFechaS(), 1));
            }
        }
        return chart_val;
    }

    private List<Groupby> groupbyIP(List<Visita> visits) {
        List<Groupby> chart_val = new ArrayList<>();
        for (Visita visit :
                visits) {
            auxi = find(chart_val, visit.getIp());
            if (auxi != -1) {
                auxi2 = chart_val.get(auxi).getValue();
                chart_val.get(auxi).setValue(auxi2 + 1);
            } else {
                chart_val.add(new Groupby(visit.getIp(), 1));
            }
        }
        return chart_val;
    }

    public int find(List<Groupby> array, String target) {
        if (array.size() == 0)
            return -1;

        int i = 0, return_val = -1;
        while (i < array.size() && return_val == -1) {
            if (array.get(i).getName() == target)
                return_val = i;
            i++;
        }
        return return_val;
    }

}
