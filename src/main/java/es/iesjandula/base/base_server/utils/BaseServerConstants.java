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
	public static final int ERR_GENERIC_EXCEPTION_CODE 	 = 0 ;
	
	/** Error - Excepción genérica - Mensaje */
	public static final String ERR_GENERIC_EXCEPTION_MSG = "Excepción genérica en " ;
	
	/** Error - Error mientras se obtenía la clave pública */
	public static final int ERR_GETTING_PUBLIC_KEY	 	 = 1 ;
	
	
	/*********************************************************/
	/**************** Colección - Usuarios *******************/
	/*********************************************************/
	
	/** Collection Usuarios - Attribute Roles */
	public static final String COLLECTION_USUARIOS_ATTRIBUTE_ROLES = "roles" ;
	
	
	/*********************************************************/
	/************************ Roles **************************/
	/*********************************************************/
	
	/** Role - Administrador */
	public static final String ROLE_ADMINISTRADOR = "ADMINISTRADOR" ;
	
	/** Role - Profesor */
	public static final String ROLE_PROFESOR 	  = "PROFESOR" ;
	
	/** Role - Dirección */
	public static final String ROLE_DIRECCION 	  = "DIRECCION" ;
}
