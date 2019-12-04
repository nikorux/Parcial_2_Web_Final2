package edu.pucmm.parcial2.Encapsulaciones;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class Url implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String url;
    private String redirect;
    private String ip;

    @ManyToOne
    private Usuario usuario;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "url")
    private Set<Visita> visits;

    public Url() {
    }

    public Url(String url, String redirect,  String browser, String so, String ip) {
        this.url = url;
        this.redirect = redirect;
        this.ip = ip;
        this.usuario = null;
    }

    public Url(String url, String ip, String redirect, Usuario usuario) {
        this.url = url;
        this.redirect = redirect;
        this.ip = ip;
        this.usuario = usuario;
    }

    public Set<Visita> getVisits() {
        return visits;
    }

    public void setVisits(Set<Visita> visits) {
        this.visits = visits;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
