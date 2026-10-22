/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.laboratorio1;

import Entity.HibernateUtil;
import Entity.Nave;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author DAM2P
 */
public class HangarControl {
    
    
   public void registrarNuevaNave(Nave nave){
       // Declaramos la Session y la Transaction
        Transaction tx = null;
        // 2. Abrimos la sesión usando try-with-resources para garantizar el cierre seguro
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            // Iniciamos la transacción (Obligatorio en Hibernate para operaciones de escritura DML)
            tx = session.beginTransaction();

            System.out.println("⏳ Persistiendo la nave en la base de datos relacional...");
            
            // Guardo el objeto con el metodo persist()
            session.persist(nave);

            // Confirmamos los cambios en PostgreSQL
            tx.commit();
            
            System.out.println("✔ Transmisión guardada con éxito.");
            // Demostración del fin del desfase: ¡El objeto Java ya tiene el ID asignado por Postgres!
            System.out.println("🚀 ID generado automáticamente por PostgreSQL: " + nave.getId());
            System.out.println(nave);

        } catch (Exception e) {
            // Si algo falla (p.ej. error de contraseña), deshacemos los cambios para proteger la BD
            if (tx != null) {
                tx.rollback();
            }
            System.err.println("❌ Error en la persistencia del bloque: " + e.getMessage());
            e.printStackTrace();
        }     
   }
   
   
   
   
   
    public void actualizarEstadoNave(int idNave, String nuevoEstado)  {
       System.out.println("Buscando Nave en PostgreSQL...");
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // El método get busca por Clase y Clave Primaria. Retorna null si no existe.
            Nave naveRecuperada = session.get(Nave.class, idNave);
            
            if (naveRecuperada != null) {
                // Al recuperarse de la sesión, el objeto entra automáticamente en estado PERSISTENTE
                System.out.println("✔ Nave localizada en el hangar: " + naveRecuperada);
                
                // ============================================================
                // OP 2: MODIFICACIÓN (Dirty Checking de Estado Persistente)
                // ============================================================
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    
                    System.out.println("Modificando estado del sistema...");
                    // Cambiamos el atributo en Java puro
                    naveRecuperada.setEstadoSistema(nuevoEstado);
                    
                    // ¡OJO ALUMNOS!: No llamamos a session.update() ni a session.persist(). 
                    // Al estar en estado PERSISTENTE, Hibernate detecta el cambio ("dirty checking")
                    // y generará el UPDATE de SQL de forma automática al hacer commit.
                    tx.commit();
                    System.out.println("✔ Base de datos actualizada dinámicamente.");
                    
                } catch (Exception e) {
                    if (tx != null) tx.rollback();
                    System.err.println("Error en la modificación: " + e.getMessage());
                }
                
            } else {
                System.out.println("❌ No se encontró ninguna nave con el ID especificado.");
            }
        }
   }
    
    
    
    public List<Nave> obtenerCazasPorModelo(String modeloBuscado){
        String hqlFiltro = "FROM Nave n WHERE n.modelo = :modeloRef";
    
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Query<Nave> queryFiltro = session.createQuery(hqlFiltro, Nave.class);
            queryFiltro.setParameter("modeloRef", modeloBuscado);

            // Retornamos directamente el resultado (si no hay datos, getResultList() devuelve [])
            return queryFiltro.getResultList();

        } catch (Exception e) {
            System.err.println("❌ Error en el filtrado perimetral HQL: " + e.getMessage());
            return List.of(); // Devuelve lista vacía 
        }
        
    } 
}
