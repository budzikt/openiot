package org.openiot.lsm.server;
/**
*    Copyright (c) 2011-2014, OpenIoT
*
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import org.openiot.lsm.beans.Observation;
import org.openiot.lsm.beans.RDFTuple;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.schema.LSMSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Hoan Nguyen Mau Quoc
 *
 */

public class LSMTripleStore implements LSMServer {
    static final int BUFFER_SIZE = 4096;
	String RDFServletURL;
	String ObjectServletURL;
    String UPLOAD_URL;
    private String openiot_ServerHost;

  //logger
    final static Logger logger = LoggerFactory.getLogger(LSMTripleStore.class);

    public String getOpeniot_ServerHost() {
		return openiot_ServerHost;
	}

	public LSMTripleStore(String serverHost){
    	try {
    		this.openiot_ServerHost = serverHost;
			RDFServletURL = openiot_ServerHost + "rdfservlet";
			ObjectServletURL = openiot_ServerHost + "objservlet";
		    UPLOAD_URL = openiot_ServerHost + "upload";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


	@Override
	public void sensorDelete(String sensorURL,String graphURL,String clientId, String token) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
        ObjectOutputStream dos = null;
        String api = "3";
        String urlString = RDFServletURL;
        try{
            URL url = new URL(urlString);

      // Open a HTTP connection to the URL

         conn = (HttpURLConnection) url.openConnection();
         conn.setDoInput(true);
         conn.setDoOutput(true);
         conn.setUseCaches(false);

         // Use a post method.
         conn.setRequestMethod("POST");
         conn.setRequestProperty("api", api);
         conn.setRequestProperty("apiType", "delete");
         conn.setRequestProperty("Connection", "Keep-Alive");
         conn.setRequestProperty("Content-Type", "text/html");
         conn.setRequestProperty("graphURL", graphURL);
         conn.setRequestProperty("clientId", clientId);
         conn.setRequestProperty("token", token);

         dos = new ObjectOutputStream( conn.getOutputStream() );
         dos.writeObject(sensorURL);
         dos.flush();
         dos.close();

         int responseCode = conn.getResponseCode();
	     if (responseCode == HttpURLConnection.HTTP_OK) {
	            // reads server's response
	    	 BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    conn.getInputStream()));
	         String response = reader.readLine();
	         logger.info(response);
	     } else {
	    	 logger.error("Server returned non-OK code: " + responseCode);
	     }
        }catch (Exception ex) {
        	logger.error("sensorDelete returns error",ex);
        }
	}

	@Override
	public void deleteAllReadings(String sensorURL,String graphURL,String clientId, String token) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
        String api = "4";
        String urlString = RDFServletURL;
        try{
            URL url = new URL(urlString);

      // Open a HTTP connection to the URL

         conn = (HttpURLConnection) url.openConnection();
         conn.setDoInput(true);
         conn.setDoOutput(true);
         conn.setUseCaches(false);

         // Use a post method.
         conn.setRequestMethod("POST");
         conn.setRequestProperty("api", api);
         conn.setRequestProperty("apiType", "delete");
         conn.setRequestProperty("Connection", "Keep-Alive");
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         conn.setRequestProperty("graphURL", graphURL);
         conn.setRequestProperty("clientId", clientId);
         conn.setRequestProperty("token", token);

         dos = new ObjectOutputStream( conn.getOutputStream() );
         dos.writeObject(sensorURL);
         dos.flush();
         dos.close();

         int responseCode = conn.getResponseCode();
	     if (responseCode == HttpURLConnection.HTTP_OK) {
	            // reads server's response
	    	 BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    conn.getInputStream()));
	         String response = reader.readLine();
	         logger.info(response);
	     } else {
	         logger.error("Server returned non-OK code: " + responseCode);
	     }
        }catch (Exception ex) {
        	logger.error("deleteALlReadings returns error",ex);
        }
	}

	@Override
	public void deleteAllReadings(String sensorURL, String graphURL,String dateOperator,
			Date fromTime, Date toTime,String clientId, String token) {
		// TODO Auto-generated method stub
				HttpURLConnection conn = null;
		        ObjectOutputStream dos = null;
		        String api = "delete";
		        String urlString = RDFServletURL;
		        try{
		            URL url = new URL(urlString);
		      // Open a HTTP connection to the URL

		         conn = (HttpURLConnection) url.openConnection();
		         conn.setDoInput(true);
		         conn.setDoOutput(true);
		         conn.setUseCaches(false);

		         // Use a post method.
		         conn.setRequestMethod("POST");
		         conn.setRequestProperty("api", api);
		         conn.setRequestProperty("apiType", "delete");
		         conn.setRequestProperty("Connection", "Keep-Alive");
		         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		         conn.setRequestProperty("graphURL", graphURL);
		         conn.setRequestProperty("dateOperator", dateOperator);
		         conn.setRequestProperty("fromTime", fromTime.toString());
		         conn.setRequestProperty("toTime", toTime.toString());
		         conn.setRequestProperty("clientId", clientId);
		         conn.setRequestProperty("token", token);

		         dos = new ObjectOutputStream( conn.getOutputStream() );
		         dos.writeObject(sensorURL);
		         dos.flush();
		         dos.close();

		         int responseCode = conn.getResponseCode();
			     if (responseCode == HttpURLConnection.HTTP_OK) {
			            // reads server's response
			    	 BufferedReader reader = new BufferedReader(new InputStreamReader(
			                    conn.getInputStream()));
			         String response = reader.readLine();
			         logger.info(response);
//			         System.out.println(response);
			     } else {
			         logger.error("Server returned non-OK code: " + responseCode);
			     }
		        }catch (Exception ex) {
		            logger.error("deleteAllReadings returns error",ex);
		        }
	}

	@Override
	public Sensor getSensorById(String sensorURL,String graphURL,String clientId, String token) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
        String api = "5";
        String urlString = RDFServletURL;
        Sensor sensor = null;
        try{
            URL url = new URL(urlString);

      // Open a HTTP connection to the URL

         conn = (HttpURLConnection) url.openConnection();
         conn.setDoInput(true);
         conn.setDoOutput(true);
         conn.setUseCaches(false);

         // Use a post method.
         conn.setRequestMethod("POST");
         conn.setRequestProperty("api", api);
         conn.setRequestProperty("apiType", "get");
         conn.setRequestProperty("Connection", "Keep-Alive");
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         conn.setRequestProperty("graphURL", graphURL);
         conn.setRequestProperty("clientId", clientId);
         conn.setRequestProperty("token", token);

         dos = new ObjectOutputStream( conn.getOutputStream() );
         dos.writeObject(sensorURL);
         dos.flush();
         dos.close();

      // always check HTTP response code from server
	     int responseCode = conn.getResponseCode();
	     if (responseCode == HttpURLConnection.HTTP_OK) {
	         ObjectInputStream outputFromServlet =new ObjectInputStream(conn.getInputStream());
	         sensor = (Sensor) outputFromServlet .readObject();
	         logger.info("sensor Id return:"+sensor.getId());
	     }else {
	         logger.error("Server returned non-OK code: " + responseCode);
	     }
        }catch (Exception ex) {
//        	ex.printStackTrace();
            logger.error("getSensorById returns error",ex);
        }
		return sensor;
	}

	/**
	 * add new Sensor
	 *
	 */
	@Override
	public String sensorAdd(Sensor sensor, String clientId, String token) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
        ObjectOutputStream dos = null;
        int responseCode = 0;
        String idReturn = "";
        String api = "21";
        String urlString = ObjectServletURL;
        try{
            URL url = new URL(urlString);

      // Open a HTTP connection to the URL

         conn = (HttpURLConnection) url.openConnection();
         conn.setDoInput(true);
         conn.setDoOutput(true);
         conn.setUseCaches(false);

         // Use a post method.
         conn.setRequestMethod("POST");
         conn.setRequestProperty("api", api);
         conn.setRequestProperty("apiType", "insert");
         conn.setRequestProperty("Connection", "Keep-Alive");
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         conn.setRequestProperty("clientId", clientId);
         conn.setRequestProperty("token", token);

         dos = new ObjectOutputStream( conn.getOutputStream() );
         dos.writeObject(sensor);
         dos.flush();
         dos.close();

         responseCode = conn.getResponseCode();
	     if (responseCode == HttpURLConnection.HTTP_OK) {
	    	 BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    conn.getInputStream()));
	         String response = reader.readLine();
	         logger.info(response);
	         logger.info("sensor id returns:"+sensor.getId());
	         idReturn = sensor.getId();
	     }else {
		     logger.error("Server returned non-OK code: " + responseCode);
		 }
        }catch (Exception ex) {
//        	ex.printStackTrace();
            logger.error("sensorAdd returns error",ex);
        }
        return idReturn;
	}

	@Override
	public void sensorDataUpdate(Observation observation,String clientId, String token){
		// TODO Auto-generated method stub
			HttpURLConnection conn = null;
	        ObjectOutputStream dos = null;
	        String api = "22";
	        String urlString = ObjectServletURL;
	        try{
	            URL url = new URL(urlString);

	      // Open a HTTP connection to the URL

	         conn = (HttpURLConnection) url.openConnection();
	         conn.setDoInput(true);
	         conn.setDoOutput(true);
	         conn.setUseCaches(false);

	         // Use a post method.
	         conn.setRequestMethod("POST");
	         conn.setRequestProperty("api", api);
	         conn.setRequestProperty("apiType", "insert");
	         conn.setRequestProperty("Connection", "Keep-Alive");
	         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	         conn.setRequestProperty("clientId", clientId);
	         conn.setRequestProperty("token", token);

	         dos = new ObjectOutputStream( conn.getOutputStream() );
//	         System.out.println(observation.getId());
	         dos.writeObject(observation);
	         dos.flush();
	         dos.close();

	         int responseCode = conn.getResponseCode();
		     if (responseCode == HttpURLConnection.HTTP_OK) {
		    	 BufferedReader reader = new BufferedReader(new InputStreamReader(
		                    conn.getInputStream()));
		         String response = reader.readLine();
		         logger.info(response);
		         logger.info("Sensor data is updated successfully");
		         logger.info("Please use OpenIoT LSM Sparql Endpoint to check it");
		     }else {
			     logger.error("Server returned non-OK code: " + responseCode);
			 }
	        }catch (Exception ex) {
//	        	ex.printStackTrace();
	            logger.error("sensorDataUpdate return error",ex);
	        }
	}

	@Override
	public boolean pushRDF(String graphURL,String triples, String clientId, String token) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
        ObjectOutputStream dos = null;
        String api = "23";
        String urlString = ObjectServletURL;
        try{
            URL url = new URL(urlString);

      // Open a HTTP connection to the URL

         conn = (HttpURLConnection) url.openConnection();
         conn.setDoInput(true);
         conn.setDoOutput(true);
         conn.setUseCaches(false);

         // Use a post method.
         conn.setRequestMethod("POST");
         conn.setRequestProperty("api", api);
         conn.setRequestProperty("apiType", "insert");
         conn.setRequestProperty("Connection", "Keep-Alive");
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         conn.setRequestProperty("graphURL", graphURL);
         conn.setRequestProperty("clientId", clientId);
         conn.setRequestProperty("token", token);

         RDFTuple tuple = new RDFTuple(graphURL,triples);
         dos = new ObjectOutputStream( conn.getOutputStream() );
         dos.writeObject(tuple);
         dos.flush();
         dos.close();

         int responseCode = conn.getResponseCode();
	     if (responseCode == HttpURLConnection.HTTP_OK) {
	    	 BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    conn.getInputStream()));
	         String response = reader.readLine();
	         logger.info(response);
	         logger.info("Please use OpenIoT LSM Sparql Endpoint to check it");
	         return true;
	     }else {
		     logger.error("Server returned non-OK code: " + responseCode);
		 }
        }catch (Exception ex) {
            logger.error("cannot send the data to LSM Server",ex);
        }
        return false;
	}


	@Override
	public void deleteTriples(String graphURL, String triples,String clientId, String token) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
        ObjectOutputStream dos = null;
        String api = "24";
        String urlString = ObjectServletURL;
        try{
            URL url = new URL(urlString);

      // Open a HTTP connection to the URL

         conn = (HttpURLConnection) url.openConnection();
         conn.setDoInput(true);
         conn.setDoOutput(true);
         conn.setUseCaches(false);

         // Use a post method.
         conn.setRequestMethod("POST");
         conn.setRequestProperty("api", api);
         conn.setRequestProperty("apiType", "insert");
         conn.setRequestProperty("Connection", "Keep-Alive");
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         conn.setRequestProperty("graphURL", graphURL);
         conn.setRequestProperty("clientId", clientId);
         conn.setRequestProperty("token", token);

         RDFTuple tuple = new RDFTuple(graphURL,triples);
         dos = new ObjectOutputStream( conn.getOutputStream() );
         dos.writeObject(tuple);
         dos.flush();
         dos.close();

         int responseCode = conn.getResponseCode();
	     if (responseCode == HttpURLConnection.HTTP_OK) {
	    	 BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    conn.getInputStream()));
	         String response = reader.readLine();
	         logger.info(response);
	     }else {
		     logger.error("Server returned non-OK code: " + responseCode);
		 }
        }catch (Exception ex) {
//        	ex.printStackTrace();
            logger.error("cannot send the data to LSM Server",ex);
        }
	}

	@Override
	public void deleteTriples(String graphURL,String clientId, String token) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
        ObjectOutputStream dos = null;
        String api = "24";
        String urlString = ObjectServletURL;
        try{
            URL url = new URL(urlString);

      // Open a HTTP connection to the URL

         conn = (HttpURLConnection) url.openConnection();
         conn.setDoInput(true);
         conn.setDoOutput(true);
         conn.setUseCaches(false);

         // Use a post method.
         conn.setRequestMethod("POST");
         conn.setRequestProperty("api", api);
         conn.setRequestProperty("apiType", "insert");
         conn.setRequestProperty("Connection", "Keep-Alive");
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         conn.setRequestProperty("clientId", clientId);
         conn.setRequestProperty("token", token);

         RDFTuple tuple = new RDFTuple(graphURL,"all");
         dos = new ObjectOutputStream( conn.getOutputStream() );
         dos.writeObject(tuple);
         dos.flush();
         dos.close();

         int responseCode = conn.getResponseCode();
	     if (responseCode == HttpURLConnection.HTTP_OK) {
	    	 BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    conn.getInputStream()));
	         String response = reader.readLine();
	         logger.info(response);
	     }else {
		     logger.error("Server returned non-OK code: " + responseCode);
		 }
        }catch (Exception ex) {
//        	ex.printStackTrace();
            logger.error("cannot send the data to LSM Server",ex);
        }
	}

	@Override
	public void uploadSchema(LSMSchema schema,String name,String clientId, String token) {
		// TODO Auto-generated method stub
		try{
			URL url = new URL(UPLOAD_URL);
	        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
	        httpConn.setUseCaches(false);
	        httpConn.setDoOutput(true);
	        httpConn.setRequestMethod("POST");
	        // sets file name as a HTTP header
	        httpConn.setRequestProperty("fileName", name);
	        httpConn.setRequestProperty("project", "openiot");
	        httpConn.setRequestProperty("clientId", clientId);
	        httpConn.setRequestProperty("token", token);
	        // opens output stream of the HTTP connection for writing data
	        OutputStream outputStream = httpConn.getOutputStream();

	        logger.info("Start writing data...");
	        schema.getBase().write(outputStream,"RDF/XML");
	        logger.info("Data was written.");
	        outputStream.close();

	        // always check HTTP response code from server
	        int responseCode = httpConn.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            // reads server's response
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    httpConn.getInputStream()));
	            String response = reader.readLine();
	            logger.info("Server's response: " + response);
	        } else {
	            logger.error("Server returned non-OK code: " + responseCode);
	        }
		}catch(Exception e){
//			e.printStackTrace();
			logger.error("uploadSchema returns error",e);
		}
	}

	@Override
	public void updateTriples(String graphURL, String newTriplePatterns, String oldTriplePatterns,String clientId, String token) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
        ObjectOutputStream dos = null;
        int responseCode = 0;
        try{
             URL url = new URL(ObjectServletURL);
	      // Open a HTTP connection to the URL

	         conn = (HttpURLConnection) url.openConnection();
	         conn.setDoInput(true);
	         conn.setDoOutput(true);
	         conn.setUseCaches(false);

	         // Use a post method.
	         conn.setRequestMethod("POST");
	         conn.setRequestProperty("graphURL",graphURL);
		     conn.setRequestProperty("project", "openiot");
		     conn.setRequestProperty("operator", "delete");
	         conn.setRequestProperty("Connection", "Keep-Alive");
	         conn.setRequestProperty("Content-Type", "text/html");
	         conn.setRequestProperty("clientId", clientId);
	         conn.setRequestProperty("token", token);

	         HashMap<String, String> patterns = new HashMap<String, String>();
	         patterns.put("delete", oldTriplePatterns);
	         patterns.put("update", newTriplePatterns);
	         dos = new ObjectOutputStream( conn.getOutputStream() );
	         dos.writeObject(patterns);
	         dos.flush();
	         dos.close();

	      // always check HTTP response code from server
	        responseCode = conn.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    conn.getInputStream()));
	            String response = reader.readLine();
	            logger.debug("Server's response: {}.", response);
	        } else {
	            logger.debug("Server returned non-OK code: {}.", responseCode);
	        }
        }catch (Exception ex) {
            logger.error("cannot send data to server", ex);
        }
	}

}
