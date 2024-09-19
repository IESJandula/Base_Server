package es.iesjandula.base.base_server.utils;

/**
 * @author Francisco Manuel Benítez Chico
 */
public class BaseServerConstants
{
	/*********************************************************/
	/*********************** Errores *************************/
	/*********************************************************/
	
	/** Error - Excepción genérica - Código */
	public static final int ERR_GENERIC_EXCEPTION_CODE 	 		= 0 ;
	
	/** Error - Excepción genérica - Mensaje */
	public static final String ERR_GENERIC_EXCEPTION_MSG 		= "Excepción genérica en " ;
	
	/** Error - Error mientras se obtenía la clave pública */
	public static final int ERR_GETTING_PUBLIC_KEY	 	 		= 1 ;
	
	/** Error - Error mientras se obtenía el token personalizado JWT */
	public static final int ERR_GETTING_PERSONALIZED_TOKEN_JWT  = 2 ;
	
	/** Error - Error mientras se manejaban los recursos */
	public static final int EXC_ERR_CODE_RESOURCES_HANDLER 		= 3 ;
	
	
	/*********************************************************/
	/**************** Colección - Usuarios *******************/
	/*********************************************************/
	
	/** Collection Usuarios - Attribute Email */
	public static final String COLLECTION_USUARIOS_ATTRIBUTE_EMAIL     = "email" ;
	
	/** Collection Usuarios - Attribute Nombre */
	public static final String COLLECTION_USUARIOS_ATTRIBUTE_NOMBRE    = "nombre" ;
	
	/** Collection Usuarios - Attribute Apellidos */
	public static final String COLLECTION_USUARIOS_ATTRIBUTE_APELLIDOS = "apellidos" ;
	
	/** Collection Usuarios - Attribute Roles */
	public static final String COLLECTION_USUARIOS_ATTRIBUTE_ROLES 	   = "roles" ;
	
	
	/*********************************************************/
	/************************ Roles **************************/
	/*********************************************************/
	
	/** Role - Administrador */
	public static final String ROLE_ADMINISTRADOR 	  = "ADMINISTRADOR" ;
	
	/** Role - Profesor */
	public static final String ROLE_PROFESOR 	  	  = "PROFESOR" ;
	
	/** Role - Dirección */
	public static final String ROLE_DIRECCION 	  	  = "DIRECCION" ;
	
	/** Role - Cliente impresora */
	public static final String ROLE_CLIENTE_IMPRESORA = "CLIENTE_IMPRESORA" ;
	
	
	/*********************************************************/
	/********************** Firebase *************************/
	/*********************************************************/
	
	/** Firebase - UID */
	public static final String FIREBASE_UID 	  = "uid" ;
}
