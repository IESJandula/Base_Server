package es.iesjandula.base.base_server.jar_update;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Service
@Configuration
@EnableScheduling
@Slf4j
public class JarUpdateService
{
	/** Atributo - Ruta absoluta al JAR */
	private String rutaAbsolutaAlJar ;
	
	/** Atributo - JAR valido */
	private boolean jarValido ;
	
	/** Atributo - Ultima modificacion */
    private long ultimaModificacionJar ;

    @PostConstruct
    public void init()
    {
    	// Obtenemos el nombre del archivo JAR en ejecución
        this.rutaAbsolutaAlJar = JarUpdateService.class.getProtectionDomain().getCodeSource().getLocation().getPath() ;
        
        log.info("La ruta original del JAR es " + this.rutaAbsolutaAlJar) ;

        // Eliminamos prefijos y sufijos innecesarios
        this.eliminarPrefijosInnecesarios() ;
        this.eliminarSufijosInnecesarios() ;
        
        log.info("El JAR está ubicado en {}", this.rutaAbsolutaAlJar) ;
        
        File jarFile = new File(this.rutaAbsolutaAlJar) ;
        
        // Si el JAR definitivamente existe tendremos en cuenta este proceso
        this.jarValido = jarFile.exists() ;
        
        // Si el JAR es válido, hacemos la asignación de la última modificación
        if (this.jarValido)
        {
        	this.ultimaModificacionJar = jarFile.lastModified() ;
        	
        	log.info("Última modificación del JAR en el tiempo {}", this.ultimaModificacionJar) ;
        }
    }
    
    /**
     * Eliminamos prefijos innecesarios
     */
    private void eliminarPrefijosInnecesarios()
    {
        // Si el JAR tiene prefijo "file:" ...
        String pathParaEliminar = "file:" ;
        if (this.rutaAbsolutaAlJar.startsWith(pathParaEliminar))
        {
        	// ... Lo eliminamos
        	this.rutaAbsolutaAlJar = this.rutaAbsolutaAlJar.substring(pathParaEliminar.length()) ;
        	
        	log.warn("Se ha eliminado un sufijo que no nos interesa {} y ha quedado así finalmente: {}", pathParaEliminar, this.rutaAbsolutaAlJar) ;
        }
        
        // Si el JAR tiene prefijo "nested:" ...
        pathParaEliminar = "nested:" ;
        if (this.rutaAbsolutaAlJar.startsWith(pathParaEliminar))
        {
        	// ... Lo eliminamos
        	this.rutaAbsolutaAlJar = this.rutaAbsolutaAlJar.substring(pathParaEliminar.length()) ;
        	
        	log.warn("Se ha eliminado un sufijo que no nos interesa {} y ha quedado así finalmente: {}", pathParaEliminar, this.rutaAbsolutaAlJar) ;
        }
    }
    
    /**
     * Eliminamos sufijos innecesarios
     */
    private void eliminarSufijosInnecesarios()
    {
        // Si el JAR tiene sufijo después de ".jar!" ...
        String pathParaEliminar = ".jar!" ;
        if (this.rutaAbsolutaAlJar.contains(pathParaEliminar))
        {
        	// ... Lo eliminamos
        	int indexOfJar 	   	   = this.rutaAbsolutaAlJar.indexOf(pathParaEliminar) ;
        	this.rutaAbsolutaAlJar = this.rutaAbsolutaAlJar.substring(0, indexOfJar) ;
        	
        	log.warn("Se ha eliminado un sufijo que no nos interesa {} y ha quedado así finalmente: {}", pathParaEliminar, this.rutaAbsolutaAlJar) ;
        }
        
        // Si el JAR tiene sufijo de este tipo, lo quitamos: /!BOOT-INF ...
        pathParaEliminar = "/!BOOT-INF" ;
        if (this.rutaAbsolutaAlJar.contains(pathParaEliminar))
        {
        	// ... Lo eliminamos
        	int indexOfJar 	   	   = this.rutaAbsolutaAlJar.indexOf(pathParaEliminar) ;
        	this.rutaAbsolutaAlJar = this.rutaAbsolutaAlJar.substring(0, indexOfJar) ;
        	
        	log.warn("Se ha eliminado un sufijo que no nos interesa {} y ha quedado así finalmente: {}", pathParaEliminar, this.rutaAbsolutaAlJar) ;
        }
    }

    /**
     * Ejecutar cada 5 segundos
     */
    @Scheduled(fixedRate = 5000)
    public void checkJarUpdate()
    {
    	// Comprobamos si es un JAR válido
    	if (this.jarValido)
    	{
	    	File jarFile = new File(this.rutaAbsolutaAlJar) ;
	        
	    	// Verificamos si el archivo JAR ha sido actualizado
	        if (jarFile.lastModified() > this.ultimaModificacionJar)
	        {
	        	log.info("¡El JAR ha sido actualizado! Finalizando la aplicación...") ;
	
	        	// Cerramos esta aplicación
	        	System.exit(0) ;
	        }
    	}
    }
}
