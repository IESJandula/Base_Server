package es.iesjandula.base.base_server.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author Francisco Manuel Benítez Chico
 */
public class HttpClientUtils
{
	/**
     * Creamos un HttpClient con configuración de timeout
     * @param timeout timeout
     * @return CloseableHttpClient configurado
     */
    public static CloseableHttpClient crearHttpClientConTimeout(int timeout)
    {
        RequestConfig config = RequestConfig.custom()
								            .setConnectTimeout(timeout)
								            .setConnectionRequestTimeout(timeout)
								            .setSocketTimeout(timeout)
								            .build() ;

        return HttpClients.custom().setDefaultRequestConfig(config).build() ;
    }
}
