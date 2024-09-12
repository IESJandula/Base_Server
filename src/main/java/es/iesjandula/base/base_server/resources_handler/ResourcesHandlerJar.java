package es.iesjandula.base.base_server.resources_handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.iesjandula.base.base_server.utils.BaseServerConstants;
import es.iesjandula.base.base_server.utils.BaseServerException;

/**
 * @author Francisco Manuel Benítez Chico
 */
public class ResourcesHandlerJar extends ResourcesHandler
{
	/** Logger of the class */
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesHandlerJar.class);
	  
	  public ResourcesHandlerJar(URL resourceFolderUrl)
	  {
	    super(resourceFolderUrl);
	  }
	  
	  public void copyToDirectory(File destDir) throws BaseServerException
	  {
	    try
	    {
	      URLConnection urlConnection = getResourceFolderUrl().openConnection();
	      readContent(destDir, (JarURLConnection)urlConnection);
	    }
	    catch (IOException ioException)
	    {
	      String errorString = "IOException while opening the connection";
	      LOGGER.error(errorString, ioException);
	      
	      throw new BaseServerException(BaseServerConstants.EXC_ERR_CODE_RESOURCES_HANDLER, errorString, ioException);
	    } 
	  }
	  
	  private void readContent(File destDir, JarURLConnection jarURLConnection) throws BaseServerException
	  {
	    JarFile jarFile = getJarFile(jarURLConnection);
	    Enumeration<JarEntry> entryEnumeration = jarFile.entries();
	    while (entryEnumeration.hasMoreElements())
	    {
	      JarEntry entry = entryEnumeration.nextElement();
	      if (entry.getName().startsWith(jarURLConnection.getEntryName()))
	      {
	        String subfolderFile = entry.getName().replace(jarURLConnection.getEntryName(), "");
	        createFileOrDirectory(destDir, jarFile, entry, subfolderFile);
	      } 
	    } 
	  }
	  
	  private JarFile getJarFile(JarURLConnection urlConnection) throws BaseServerException
	  {
	    JarFile outcome = null;
	    try 
	    {
	      outcome = urlConnection.getJarFile();
	    }
		 catch (IOException ioException)
		  {
	      String errorString = "IOException while getting the JAR file";
	      
	      LOGGER.error(errorString, ioException);
	      throw new BaseServerException(BaseServerConstants.EXC_ERR_CODE_RESOURCES_HANDLER, errorString, ioException);
	    } 
		  
	    return outcome;
	  }
	  
	  private void createFileOrDirectory(File destDir, JarFile jarFile, JarEntry jarEntry, String subfolderFile) throws BaseServerException {
	    if (jarEntry.isDirectory())
	    {
	      this.createDirectoryIfNotExists(destDir, subfolderFile);
	    } 
	    else
	    {
	      String fileNameWithoutExtraInfo = this.getFileSubPathWithoutExtraInfoFromFullPath(subfolderFile);
	      if (fileNameWithoutExtraInfo != null)
	      {
	        InputStream inputStream = null;
	        try
	        {
	          inputStream = jarFile.getInputStream(jarEntry);
	          this.createFile(destDir, inputStream, fileNameWithoutExtraInfo);
	        } 
	        catch (IOException ioException)
	        {
	          String errorString = "IOException while getting an input stream from the file " + jarEntry.getName();
	          
	          LOGGER.error(errorString, ioException);
	          throw new BaseServerException(BaseServerConstants.EXC_ERR_CODE_RESOURCES_HANDLER, errorString, ioException);
	        }
	        finally
	        {
	          this.closeStream(inputStream);
	        } 
	      } 
	    } 
	  }
	  
	  protected String getSeparator()
	  {
	    return "/";
	  }
}
