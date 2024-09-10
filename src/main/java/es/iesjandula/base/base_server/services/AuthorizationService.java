package es.iesjandula.base.base_server.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.iesjandula.base.base_server.utils.BaseServerConstants;
import es.iesjandula.base.base_server.utils.BaseServerException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Slf4j
@Service
public class AuthorizationService
{
	@Value("${reaktor.publicKeyFile}")
	private String baseServerPublicKeyFile ;
	
	/**
	 * @param authorizationHeader authorization header (jwt)
	 * @param roleRequerido role requerido
	 */
	public void autorizarPeticion(String authorizationHeader, String roleRequerido) throws BaseServerException
	{
	    // Elimina el prefijo "Bearer " del encabezado de autorización para obtener el token JWT limpio
	    String token = authorizationHeader.replace("Bearer ", "") ;

	    // Parsea y verifica el token JWT utilizando la clave pública y obtiene los claims
	    Claims claims = Jwts.parser() // Inicializa el parser (analizador) de JWT
	                        .verifyWith(this.obtenerClavePublica()) // Configura la clave pública para validar la firma del JWT
	                        .build() // Construye el objeto del parser configurado
	                        .parseSignedClaims(token) // Parsea el JWT firmado y verifica su firma
	                        .getPayload(); // Obtiene el cuerpo (claims) del JWT
	    
	    // Extrae los roles del usuario desde los claims obtenidos
	    @SuppressWarnings("unchecked")
		List<String> roles = (List<String>) claims.get(BaseServerConstants.COLLECTION_USUARIOS_ATTRIBUTE_ROLES) ;

	    // Verifica si el usuario tiene el rol requerido para acceder al recurso
	    if (!roles.contains(roleRequerido))
	    {
	        // Si el usuario no tiene el rol requerido, prepara un mensaje de error
	        String errorString = "El usuario no tiene el role de " + roleRequerido + " para acceder al recurso" ;
	        
	        log.error(errorString) ;
	        throw new BaseServerException(BaseServerConstants.ERR_GETTING_PUBLIC_KEY, errorString) ;
	    }
	}
	
	/**
	 * @return la clave pública
	 * @throws BaseServerException con un error
	 */
	private PublicKey obtenerClavePublica() throws BaseServerException
	{
		try
		{
		    // Lee el contenido del archivo de clave pública ('public_key.pem') y lo convierte a una cadena (String)
		    String publicKeyContent = new String(Files.readAllBytes(Paths.get(this.baseServerPublicKeyFile)));
		    
		    // Elimina los saltos de línea (\n) y las etiquetas de inicio y fin de la clave pública
		    publicKeyContent = publicKeyContent.replaceAll("\\n", "") // Elimina todos los saltos de línea
					 						   .replaceAll("\\r", "")
									           .replace("-----BEGIN PUBLIC KEY-----", "")        // Elimina la etiqueta de inicio de la clave pública
									           .replace("-----END PUBLIC KEY-----", "") ;        // Elimina la etiqueta de fin de la clave pública
		    
		    // Crea una instancia de KeyFactory para el algoritmo de clave pública RSA
		    KeyFactory keyFactory = KeyFactory.getInstance("RSA") ;
		    
		    // Crea una especificación de clave pública X509 a partir de la cadena decodificada en Base64
		    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent)) ;
		    
		    // Genera una instancia de PublicKey a partir de la especificación de clave pública (keySpec)
		    return keyFactory.generatePublic(keySpec) ;
		}
		catch (IOException ioException)
		{
			String errorString = "IOException mientras se cargaba el fichero con la clave privada" ;
			
			log.error(errorString, ioException) ;
			throw new BaseServerException(BaseServerConstants.ERR_GETTING_PUBLIC_KEY, errorString, ioException) ;
		}
		catch (InvalidKeySpecException invalidKeySpecException)
		{
			String errorString = "InvalidKeySpecException mientras se cargaba el fichero con la clave privada" ;
			
			log.error(errorString, invalidKeySpecException) ;
			throw new BaseServerException(BaseServerConstants.ERR_GETTING_PUBLIC_KEY, errorString, invalidKeySpecException) ;
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException)
		{
			String errorString = "NoSuchAlgorithmException mientras se cargaba el fichero con la clave privada" ;
			
			log.error(errorString, noSuchAlgorithmException) ;
			throw new BaseServerException(BaseServerConstants.ERR_GETTING_PUBLIC_KEY, errorString, noSuchAlgorithmException) ;
		}
	}
}
