package edu.pucmm.parcial2.Servicios;

import edu.pucmm.parcial2.Encapsulaciones.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class VisitServices extends DatabaseServices<Visita> {
    private static VisitServices instancia;

    private VisitServices() {
        super(Visita.class);
    }

    public static VisitServices getInstancia() {
        if (instancia == null) {
            instancia = new VisitServices();
        }
        return instancia;
    }

    public List<Visita> getVisitbyUrl(long id) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("SELECT e FROM Visita e where url_id = :id ", Visita.class);
        query.setParameter("id", id);
        List<Visita> list = query.getResultList();
        return list;
    }
//
//    public void getVisitbyBrowser(long id) {
//        EntityManager em = getEntityManager();
//        Query query = em.createQuery("SELECT e.browser as name, count(e.id) as value FROM Visit e group by e.browser");
//        //query.setParameter("id", id);
//        List<Object[]> list = query.getResultList();
//        list.stream().forEach((record) -> {
//            String name = (String) record[0];
//            int value = (int) record[1];
//            System.out.println(name + " " + value);
//        });
//
//    }


}
