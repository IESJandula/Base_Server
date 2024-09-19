package es.iesjandula.base.base_server.firebase;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Francisco Manuel Benítez Chico
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Usuario
{
	/** Atributo - email */
	private String email ;
	
	/** Atributo - nombre */
	private String nombre ;
	
	/** Atributo - apellidos */
	private String apellidos ;
	
	/** Atributo - roles */
	private List<String> roles ;
}
