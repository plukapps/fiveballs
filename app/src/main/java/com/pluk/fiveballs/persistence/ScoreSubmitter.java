package com.pluk.fiveballs.persistence;

//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.URLEncoder;
//import java.security.InvalidKeyException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.crypto.BadPaddingException;
//import javax.crypto.Cipher;
//import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.NoSuchPaddingException;
//import javax.crypto.spec.SecretKeySpec;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
//
//import org.apache.http.Header;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import org.xml.sax.XMLReader;
//
//import android.util.Log;

/**
 * 
 * ScoreSubmit es una clase que permite la comunicación con la nube 
 * para poder obtener los scores globales y también subir un score.
 * 
 * 
 * @author marcel
 *
 */
public class ScoreSubmitter {

//	private static final String APP_NAME = "fivemore";
//
//	private static final String TAG = "SS";
//
//	private static final int timeoutMs = 15000;
//
//	public SubmitData submitScore(String username, String score){
//		return postData(APP_NAME, username, score);
//	}
//
//	public List<ScoreData> getTopScores(int pageNum, boolean weekly) throws IOException{
//		return getTopScoresXml(APP_NAME, pageNum, null, weekly);
//	}
//
//	public List<ScoreData> getTopScores(int pageNum, String countryCode, boolean weekly) throws IOException{
//		return getTopScoresXml(APP_NAME, pageNum, countryCode, weekly);
//	}
//
//	private List<ScoreData> getTopScoresXml(String appName, int pageNum, String countryCode, boolean weekly) throws IOException{
//
//		URL url;
//		try {
//
//			String urlBase = "http://www.southdroid.com/";
//
//			String urlStr = urlBase + "score?action=getscores&app=fivemore";
//			urlStr += "&page=" + String.valueOf(pageNum);
//			if (countryCode != null) {
//				urlStr += "&countryCode=" + countryCode;
//			}
//			if (weekly) {
//				urlStr += "&weekly=true";
//			}
//
//			url = new URL(urlStr);
//
//			/* Obtengo el parser */
//			SAXParserFactory spf = SAXParserFactory.newInstance();
//			SAXParser sp = spf.newSAXParser();
//
//			/* Obtengo el XMLReader desde el SAXParser. */
//			XMLReader xr = sp.getXMLReader();
//
//			/* Creo el Hanlder del Contenido (ContentHandler) y se lo seteo al xmlReader*/
//			ScoreXmlHandler xmlHanlder = new ScoreXmlHandler();
//			xr.setContentHandler(xmlHanlder);
//
//			URLConnection conn = url.openConnection();
//			conn.setConnectTimeout(timeoutMs);
//			conn.setReadTimeout(timeoutMs);
//
//			/* Parseo el xml obtenido de la URL. */
//			xr.parse(new InputSource(conn.getInputStream()));
//			/* Terminó el parsing. */
//
//			/* Obtengo los resultados del parseo en una clase q tiene la lista con los scores*/
//			ParsedScoreDataSet parsedScoreDataSet =
//									xmlHanlder.getParsedData();
//
//
//			String nombre = parsedScoreDataSet.getExtractedString();
//			int score = parsedScoreDataSet.getExtractedInt();
//			Log.d(TAG, "Los datos parseados son: " + nombre + " " + score);
//
//			return parsedScoreDataSet.getScores();
//
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (SAXException e) {
//			e.printStackTrace();
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//		}
//
//		return null;
//
//	}
//
//
////	private void getContry(){
////		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
////
////		LocationProvider locationProvider = LocationManager.NETWORK_PROVIDER;
////		// Or use LocationManager.GPS_PROVIDER
////
////		Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
////	}
//
//
//	/**
//	 * Realiza un post con los datos del usuario y score
//	 * y retorna un String conteniendo el texto del resultado
//	 *
//	 * o null si falla
//	 *
//	 */
//	private SubmitData postData(String appName, String username, String score) {
//
//		HttpParams httpParameters = new BasicHttpParams();
//
//		/*
//		 * Seteo el timeout para la conexión
//		 */
//		int timeoutConnection = 10000;
//		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//
//		/*
//		 * Seteo el timeout para leer el socket
//		 */
//		int timeoutSocket = timeoutMs;
//		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//
//		// Create a new HttpClient and Post Header
//	    HttpClient httpclient = new DefaultHttpClient(httpParameters);
//	    HttpPost httppost = new HttpPost("http://www.southdroid.com/score");
//
//	    try {
//	        // Agrego los datos al post
//	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//	        nameValuePairs.add(new BasicNameValuePair("app", appName));
//	        nameValuePairs.add(new BasicNameValuePair("action", "submit"));
//	        nameValuePairs.add(new BasicNameValuePair("username", URLEncoder.encode(username)));
//	        nameValuePairs.add(new BasicNameValuePair("score", score));
//
//			/*
//	         * Agrego la clave
//	         */
//	        //String texto = appName + score;
//			String password = "fivemore4567uy24";
//
//			String claveEncriptada;
//			try {
//				claveEncriptada = SHA1(password + score + appName);
//				nameValuePairs.add(new BasicNameValuePair("key", URLEncoder.encode(claveEncriptada )));
//		        Log.i(TAG, "La clave encriptada es: " + claveEncriptada );
//
//			} catch (NoSuchAlgorithmException e) {
//				Log.e(TAG, "NoSuchALgorithmExceptin " + e.getLocalizedMessage());
//			}
//
//
//	        /*
//	         * Seteo la entidad del post
//	         */
//	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//
//
//	        // Execute HTTP Post Request
//	        HttpResponse response = httpclient.execute(httppost);
//	        //Log.i("SS", "La response es" + response.getAllHeaders());
//
//	        Header[] header = null;
//			if (response.containsHeader("submit_result_ok")) {
//		       	header = response.getHeaders("submit_result_ok");
//
//				Log.i(TAG, header[0].getName() + header[0].getValue());
//				Header[] extScoreIdHeader = null;
//				long extScoreId = 0;
//				if(response.containsHeader("submit_result_id")){
//					extScoreIdHeader = response.getHeaders("submit_result_id");
//					Log.i(TAG, extScoreIdHeader[0].getName() + extScoreIdHeader[0].getValue());
//					extScoreId = Long.parseLong(extScoreIdHeader[0].getValue());
//				}
//
//				return new SubmitData(header[0].getValue(), extScoreId);
//			}
//			else return null;
//
//	    } catch (ClientProtocolException e) {
//	    } catch (IOException e) {
//	    }
//		return null;
//	}
//
//	private String convertToHex(byte[] data) {
//		StringBuffer buf = new StringBuffer();
//		for (int i = 0; i < data.length; i++) {
//			int halfbyte = (data[i] >>> 4) & 0x0F;
//			int two_halfs = 0;
//			do {
//				if ((0 <= halfbyte) && (halfbyte <= 9))
//					buf.append((char) ('0' + halfbyte));
//				else
//					buf.append((char) ('a' + (halfbyte - 10)));
//				halfbyte = data[i] & 0x0F;
//			} while (two_halfs++ < 1);
//		}
//		return buf.toString();
//	}
//
//	private String SHA1(String text) throws NoSuchAlgorithmException,
//			UnsupportedEncodingException {
//		MessageDigest md;
//		md = MessageDigest.getInstance("SHA-1");
//		byte[] sha1hash = new byte[40];
//		md.update(text.getBytes("iso-8859-1"), 0, text.length());
//		sha1hash = md.digest();
//		return convertToHex(sha1hash);
//	}
//
//	public byte[] encrypt(String text, String password) {
//        try {
//        		byte[] input = text.getBytes();
//                byte[] key = password.getBytes();
//                Cipher cipher = Cipher.getInstance("AES");
//                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
//                return cipher.doFinal(input);
//
//        } catch (InvalidKeyException e) {
//                throw new RuntimeException(e);
//        } catch (NoSuchAlgorithmException e) {
//                throw new RuntimeException(e);
//        } catch (NoSuchPaddingException e) {
//                throw new RuntimeException(e);
//        } catch (IllegalBlockSizeException e) {
//                throw new RuntimeException(e);
//        } catch (BadPaddingException e) {
//                throw new RuntimeException(e);
//    }
//}
}