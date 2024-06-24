package com.pluk.fiveballs.persistence;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

//public class ScoreXmlHandler extends DefaultHandler {
//
//		private static final String OUTERTAG = "topScores";
//
//
//		private static final String MYTAG = "mytag";
//
//		private static final String TAGWITHNUMBER = "scoreEntry";
//
//		private static final String INNERTAG = "innertag";
//
//		private static final String ATTR_NAME = "name";
//
//		private static final String ATTR_SCORE = "score";
//
//		private static final String ATTR_DATE = "fecha";
//
//		private static final String ATTR_COUNTRY_CODE = "countryCode";
//
//		private static final String ATTR_COUNTRY_NAME = "countryName";
//
//
//		// ===========================================================
//		// Fields
//		// ===========================================================
//
//
//		private static final String TAG = "ScoreXmlHanlder";
//
//		@SuppressWarnings("unused")
//		private boolean in_outertag = false;
//		@SuppressWarnings("unused")
//		private boolean in_innertag = false;
//		private boolean in_mytag = false;
//
//		private ParsedScoreDataSet myParsedExampleDataSet = new ParsedScoreDataSet();
//
//		// ===========================================================
//		// Getter & Setter
//		// ===========================================================
//
//		public ParsedScoreDataSet getParsedData() {
//			return this.myParsedExampleDataSet;
//		}
//
//		// ===========================================================
//		// Methods
//		// ===========================================================
//		@Override
//		public void startDocument() throws SAXException {
//			this.myParsedExampleDataSet = new ParsedScoreDataSet();
//		}
//
//		@Override
//		public void endDocument() throws SAXException {
//			// Nothing to do
//		}
//
//		/** Gets be called on opening tags like:
//		 * <tag>
//		 * Can provide attribute(s), when xml was like:
//		 * <tag attribute="attributeValue">*/
//		@Override
//		public void startElement(String namespaceURI, String localName,
//				String qName, Attributes atts) throws SAXException {
//			if (localName.equals(OUTERTAG)) {
//				this.in_outertag = true;
//			}else if (localName.equals(INNERTAG)) {
//				this.in_innertag = true;
//			}else if (localName.equals(MYTAG)) {
//				this.in_mytag = true;
//			}else if (localName.equals(TAGWITHNUMBER)) {
//				// Extract an Attribute
//				String attrValue = atts.getValue(ATTR_SCORE);
//				int i = Integer.parseInt(attrValue);
//				myParsedExampleDataSet.setExtractedInt(i);
//
//				String attrNameValue = URLDecoder.decode(atts.getValue(ATTR_NAME));
//				myParsedExampleDataSet.setExtractedString(attrNameValue);
//
//				Date fecha = new Date();
//				String stringFecha = atts.getValue(ATTR_DATE);
//				if(stringFecha == null || stringFecha.length() < 3){
//					stringFecha = "28/08/2010";
//				}
//
//				Log.i("SS", "La fecha parseada es + " + stringFecha);
//				//11-09 21:48:50.400: INFO/SS(311): La fecha parseada es + Tue Nov 09 21:04:55 UYST 2010
//				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//				try {
//					fecha = sdf.parse(stringFecha);
//				} catch (ParseException e) {
//					Log.e(TAG, "Ocurrió un error al parsear la fecha obtenida: " + e.getMessage());
//				}
//
//				String countryCode = assertNull(atts.getValue(ATTR_COUNTRY_CODE));
//
//				String countryName = assertNull(atts.getValue(ATTR_COUNTRY_NAME));
//
//				myParsedExampleDataSet.getScores().add(new ScoreData(attrNameValue, i, fecha, countryCode, countryName));
//			}
//		}
//
//		/** Gets be called on closing tags like:
//		 * </tag> */
//		@Override
//		public void endElement(String namespaceURI, String localName, String qName)
//				throws SAXException {
//			if (localName.equals(OUTERTAG)) {
//				this.in_outertag = false;
//			}else if (localName.equals(INNERTAG)) {
//				this.in_innertag = false;
//			}else if (localName.equals(MYTAG)) {
//				this.in_mytag = false;
//			}else if (localName.equals(TAGWITHNUMBER)) {
//				// Nothing to do here
//			}
//		}
//
//		/** Gets be called on the following structure:
//		 * <tag>characters</tag> */
//		@Override
//	    public void characters(char ch[], int start, int length) {
//			if(this.in_mytag){
//	    		myParsedExampleDataSet.setExtractedString(new String(ch, start, length));
//	    	}
//	    }
//
//		private String assertNull(String attr) {
//			if ("null".equals(attr)) {
//				attr = null;
//			}
//			return attr;
//		}
//	}
