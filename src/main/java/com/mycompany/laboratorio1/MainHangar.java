package com.mycompany.laboratorio1;

import Entity.HibernateUtil;
import Entity.Nave;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author alvaro
 */
public class MainHangar {
    public static void main(String[] args) {
        
       HangarControl controlador = new HangarControl();
       //Instanciar la nave 
       Nave tie = new Nave("TIE Interceptor","TI-V2",1,"Critico");
       
       //Registrar la nave en la base de datos
       controlador.registrarNuevaNave(tie);
       
       
       //Simular una reparación
       controlador.actualizarEstadoNave(tie.getId(), "Estable");
       
       //Escaneo de todos los cazas TI-v2
       List<Nave> navesTI = new ArrayList<>();
       navesTI = controlador.obtenerCazasPorModelo("TI-V2");
       
       if(!navesTI.isEmpty()){
           for (Nave nave : navesTI) {
               System.out.println("Nave: " + nave);
           }
       } else{
           System.out.println("No hay ninguna nave que tenga ese modelo");
       }
       
       
       
       //Cerrar la conexion con la base de datos
        HibernateUtil.shutdown();
       
    }
}
