package es.iesjandula.base.base_server;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

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
        this.rutaAbsolutaAlJar = JarUpdateService.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        // Si el JAR tiene prefijo "file:"
        if (this.rutaAbsolutaAlJar.startsWith("file:"))
        {
        	// Lo eliminamos
        	this.rutaAbsolutaAlJar = this.rutaAbsolutaAlJar.substring(5) ;
        }
        
        // Si el JAR tiene sufijo después de ".jar!"
        if (this.rutaAbsolutaAlJar.contains(".jar!"))
        {
        	// Lo eliminamos
        	int lastIndexOfJar = this.rutaAbsolutaAlJar.indexOf(".jar!") ;
        	rutaAbsolutaAlJar  = this.rutaAbsolutaAlJar.substring(0, lastIndexOfJar + 4) ;
        }
        
        log.info("El JAR está ubicado en {}", this.rutaAbsolutaAlJar) ;
        
        File jarFile = new File(this.rutaAbsolutaAlJar);
        
        // Si el JAR definitivamente existe tendremos en cuenta este proceso
        this.jarValido = jarFile.exists();
        
        if (this.jarValido)
        {
        	this.ultimaModificacionJar = jarFile.lastModified() ;
        }
    }

    /**
     * Ejecutar cada 5 segundos
     */
    @Scheduled(fixedRate = 5000)
    public void checkJarUpdate()
    {
    	if (this.jarValido)
    	{
	    	File jarFile = new File(this.rutaAbsolutaAlJar) ;
	        
	    	// Verificamos si el archivo JAR ha sido actualizado
	        if (jarFile.lastModified() > this.ultimaModificacionJar)
	        {
	        	log.info("¡El JAR ha sido actualizado! Finalizando la aplicación...") ;
	
	            // Salir de la aplicación
	            System.exit(0) ;
	        }
    	}
    }
}
