package es.iesjandula.base.base_server.security.service ;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.iesjandula.base.base_server.security.models.DtoUsuario;
import es.iesjandula.base.base_server.utils.BaseServerConstants;
import es.iesjandula.base.base_server.utils.BaseServerException;
import es.iesjandula.base.base_server.utils.HttpClientUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
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
	
	@Value("${reaktor.firebase_server_url}")
	private String firebaseServerUrl ;
	
	@Value("${reaktor.uidFile}")
	private String uidFile ;
	
	@Autowired
	private SessionStorageService sessionStorageService ;
	
	/** Atributo - JWT Parser */
	private JwtParser jwtParser ;
	
	/**
	 * Inicializa la instancia de JWT Parser
	 * @throws BaseServerException con un error al leer la clave pública
	 */
	@PostConstruct
	public void init() throws BaseServerException
	{
		this.jwtParser = Jwts.parser() 								 // Inicializa el parser (analizador) de JWT
                			 .verifyWith(this.obtenerClavePublica()) // Configura la clave pública para validar la firma del JWT
                			 .build() ; 							 // Construye el objeto del parser configurado
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
	
	/**
	 * @param authorizationHeader authorization header (jwt)
	 * @return el usuario encontrado
	 */
	public DtoUsuario obtenerUsuario(String authorizationHeader)
	{
	    // Eliminamos el prefijo "Bearer " del encabezado de autorización para obtener el token JWT limpio
	    String token = authorizationHeader.substring(7) ;

	    // Parseamos y verificamos el token JWT utilizando la clave pública y obtiene los claims
	    Claims claims = this.jwtParser.parseSignedClaims(token) // Parsea el JWT firmado y verifica su firma
	                        		  .getPayload() ; 			// Obtiene el cuerpo (claims) del JWT
	    
	    // Recogemos el resto de valores
	    String email       = (String) claims.get(BaseServerConstants.COLLECTION_USUARIOS_ATTRIBUTE_EMAIL) ;
	    String nombre      = (String) claims.get(BaseServerConstants.COLLECTION_USUARIOS_ATTRIBUTE_NOMBRE) ;
	    String apellidos   = (String) claims.get(BaseServerConstants.COLLECTION_USUARIOS_ATTRIBUTE_APELLIDOS) ;
	    
	    // Extraemos los roles del usuario desde los claims obtenidos
	    @SuppressWarnings("unchecked")
	    List<String> roles = (List<String>) claims.get(BaseServerConstants.COLLECTION_USUARIOS_ATTRIBUTE_ROLES) ;

	    // Devolvemos la instancia del usuario
	    return new DtoUsuario(email, nombre, apellidos, roles) ;
	}
	
	/**
	 * Realiza una solicitud HTTP POST al otro microservicio para obtener un token personalizado
	 *
	 * @param timeout
	 * @return El token JWT obtenido del microservicio
	 * @throws BaseServerException error al obtener el token
	 */
	public String obtenerTokenPersonalizado(int timeout) throws BaseServerException
	{
	    // Verificamos si ya tenemos un token en "sesión"
		String token = this.sessionStorageService.getToken() ;

		if (token == null || this.tokenExpirado(token))
		{
			CloseableHttpClient closeableHttpClient 	= HttpClientUtils.crearHttpClientConTimeout(timeout) ;
		    CloseableHttpResponse closeableHttpResponse = null ;

		    try
		    {
			    // Creamos una solicitud HTTP POST a nuestro microservicio Firebase
			    HttpPost postRequest = new HttpPost(this.firebaseServerUrl + "/firebase/jwt/getCustomToken") ;
		    	
			    // Añadimos el UID al encabezado de la solicitud con el valor del fichero
			    postRequest.addHeader(BaseServerConstants.FIREBASE_UID, Files.readString(Paths.get(this.uidFile)).trim()) ;
		    	
		        // Ejecutamos la solicitud HTTP
		        closeableHttpResponse = closeableHttpClient.execute(postRequest) ;

		        // Verificamos el estado de la respuesta HTTP
		        int statusCode = closeableHttpResponse.getStatusLine().getStatusCode() ;
		        
		        if (statusCode != 200)
		        {
		            // Si la respuesta no es exitosa, lanza una excepción
		            String errorString = "Error al obtener el token JWT. El código de respuesta es: " + statusCode ;
		            
		            log.error(errorString) ;
		            throw new BaseServerException(BaseServerConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString) ;
		        }

		        // Si el código de respuesta es 200, obtenemos el contenido del cuerpo, que es el token JWT
	            token = EntityUtils.toString(closeableHttpResponse.getEntity()) ;

	            // Almacenar el nuevo token en la "sesión"
	            this.sessionStorageService.setToken(token) ;
		    }
			catch (SocketTimeoutException socketTimeoutException)
			{
				String errorString = "SocketTimeoutException de lectura o escritura al comunicarse con el servidor (token JWT)" ;
				
				log.error(errorString, socketTimeoutException) ;
				throw new BaseServerException(BaseServerConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString, socketTimeoutException) ;
	        }
			catch (ConnectTimeoutException connectTimeoutException)
			{
				String errorString = "ConnectTimeoutException al intentar conectar con el servidor (token JWT)" ;
				
				log.error(errorString, connectTimeoutException) ;
				throw new BaseServerException(BaseServerConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString, connectTimeoutException) ;
	        }
		    catch (IOException ioException)
		    {
		        String errorString = "IOException mientras se obtenía el token JWT del servidor" ;
		        
		        log.error(errorString, ioException) ;
		        throw new BaseServerException(BaseServerConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString, ioException) ;
		    }
		    finally
		    {
		    	if (closeableHttpResponse != null)
		    	{
		    		try
		    		{
						closeableHttpResponse.close() ;
					}
		    		catch (IOException ioException)
		    		{
		    	        String errorString = "IOException mientras se cerraba la respuesta al obtener el token JWT del servidor" ;
		    	        
		    	        log.error(errorString, ioException) ;
		    	        throw new BaseServerException(BaseServerConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString, ioException) ;
					}
		    	}
		    	
				try
				{
					// Cerramos el CloseableHttpClient
					closeableHttpClient.close() ;
				}
				catch (IOException ioException)
				{
					String errorString = "Error al cerrar CloseableHttpClient: " + ioException.getMessage() ;
					
					log.error(errorString, ioException) ;
	    	        throw new BaseServerException(BaseServerConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString, ioException) ;
				}
		    }
		}
		
		return token ;
	}
	
	/**
	 * Verificar si el token JWT ha expirado
	 *
	 * @param token El token JWT
	 * @return true si el token ha expirado, de lo contrario, false
	 */
	public boolean tokenExpirado(String token)
	{
		try
		{
		    // Parseamos y decodificamos el token JWT utilizando la clave pública y obtenemos los claims
		    Claims claims = this.jwtParser.parseSignedClaims(token) // Parsea el JWT firmado y verifica su firma
		                        		  .getPayload() ; 			// Obtiene el cuerpo (claims) del JWT
	
			// Obtenemos la fecha de expiración del token
			Date expirationDate = claims.getExpiration() ;
	
			// Obtenemos el tiempo actual
			Date now = new Date() ;
	
			// Devuelve true si el token ha expirado, de lo contrario, false
			return expirationDate.before(now) ;
		}
		catch (Exception exception)
		{
			// Si ocurre una excepción (por ejemplo, token inválido), lo consideramos expirado
			return true ;
		}
	}
}
