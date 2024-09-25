package es.iesjandula.base.base_server.security ;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import es.iesjandula.base.base_server.security.models.DtoUsuario;
import es.iesjandula.base.base_server.security.service.AuthorizationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Francisco Manuel Benítez Chico
 * 
 * OncePerRequestFilter es una clase abstracta de Spring que asegura que el filtro se ejecuta una sola vez por solicitud HTTP
 * La idea es evitar que el filtro se ejecute múltiples veces durante una única solicitud. Por ejemplo, si nuestra aplicación realiza 
 * varias llamadas o redirecciones, este filtro solo se aplicará una vez por cada solicitud
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter
{
    @Autowired
    private AuthorizationService authorizationService ;

    /**
     * @param request con la petición de entrada
     * @param response con la petición de salida
     * @param chain con la cadena de filtros
     * @throws ServletException con un error relacionado con la cadena de filtros
     * @throws IOException con un error relacionado con la cadena de filtros
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
    			   throws ServletException, IOException
    {
        String requestURI = request.getRequestURI();  // Obtener la URI de la solicitud actual
        
        // Si la URI es "/firebase/users/authorization" o "/firebase/jwt/getCustomToken", no ejecutar el filtro
        if (requestURI.equals("/firebase/users/authorization") || requestURI.equals("/firebase/jwt/getCustomToken")) {
            chain.doFilter(request, response); // Continuar sin aplicar el filtro
            return; // Salir del filtro para estas rutas
        }
    	
    	// Obtemos el valor de cabecera de "Authorization"
        final String authorizationHeader = request.getHeader("Authorization") ;

        // Comprobamos que viene relleno
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
        {
            // Usamos el método "obtenerUsuario" para extraer los datos del usuario del JWT
            DtoUsuario usuario = this.authorizationService.obtenerUsuario(authorizationHeader) ;

            // Creamos la lista de roles, basándonos en los recibidos, para que Spring Security los utilice
            List<GrantedAuthority> authorities = usuario.getRoles().stream()
											                       .map(SimpleGrantedAuthority::new)
											                       .collect(Collectors.toList()) ;

            // Creamos un objeto de autenticación con los datos del usuario
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null, authorities) ;

            // Establecemos la autenticación en el contexto de seguridad de Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Esta línea permite que el procesamiento de la solicitud y la respuesta continúe pasando a lo largo de la cadena de filtros 
        // tales como UsernamePasswordAuthenticationFilter, SecurityContextPersistenceFilter, etcétera
        chain.doFilter(request, response) ;
    }
}



