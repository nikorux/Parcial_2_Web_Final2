package edu.pucmm.parcial2.Servicios;

import edu.pucmm.parcial2.Encapsulaciones.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class UserServices  extends DatabaseServices<Usuario> {
    private static UserServices instancia;
    int pageSize = 5;
    private UserServices() {
        super(Usuario.class);
    }

    public static UserServices getInstancia() {
        if (instancia == null) {
            instancia = new UserServices();
        }
        return instancia;
    }

    public Usuario getUser(String username, String password) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select e from Usuario e where e.username like :username and e.password like :password", Usuario.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        List<Usuario> list = query.getResultList();
        if (list.size() >0)
            return list.get(0);
        else
            return null;

    }

    public Usuario getUser(String username) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select e from Usuario e where e.username like :username", Usuario.class);
        query.setParameter("username", username);
        List<Usuario> list = query.getResultList();
        if (list.size() >0)
            return list.get(0);
        else
            return null;

    }


}
