package stock;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public MyServlet() {
        super();
    }
    
    public static String replaceAll(String source, String toReplace, String replacement) {
    	int idx = source.lastIndexOf( toReplace );
    	if ( idx != -1 ) {
    	StringBuffer ret = new StringBuffer( source );
    	ret.replace( idx, idx+toReplace.length(), replacement );
    	while( (idx=source.lastIndexOf(toReplace, idx-1)) != -1 ) {
    	ret.replace( idx, idx+toReplace.length(), replacement );
    	}
    	source = ret.toString();
    	}

    	return source;
    	} 
   
    public static void main(String[] args) {
		//String s = "SandRidge MLP tax request snared by IRS \"pause\"";
		//s = replaceAll(s, "\"", "\\\"");
		//s= replaceAll(s, "\"", ",");
		//s= replaceAll(s, "\\", "");
		//System.out.println(s);
	}
    
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
        throws ServletException, IOException {
    	response.setContentType("text/html");
        PrintWriter out = null;
        String symbol = request.getParameter("symbol");
        try{
        	out = response.getWriter();
           	String urlString = "http://default-environment-iwtzmrhxyu.elasticbeanstalk.com/?symbol="+URLEncoder.encode(symbol, "UTF-8");
        	
        	
        	URL url = new URL(urlString);
			URLConnection urlConnection	= url.openConnection();
			urlConnection.setAllowUserInteraction(false);
			InputStream urlStream = url.openStream();
			
			// using DOM to parse
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;
			Document doc = null;
			
			String jsonString = "{\"result\":{";
			
			try
			{
				dBuilder = dbFactory.newDocumentBuilder();	
				doc = dBuilder.parse(urlStream);
				
				//normalizing the document
				doc.getDocumentElement().normalize();
			} 
			catch (ParserConfigurationException e) 
			{
				String error_string = "{\"result\":{\"error\":\"ParserConfigurationException\"}}";
				out.println(error_string);
				return;
			}
			catch (SAXException e)
			{
				String error_string = "{\"result\":{\"error\":\"SAXException\"}}";
				out.println(error_string);
				return;
			}
			
			//XML has been fetched. We will check for errors before proceeding.
			
			NodeList errorList = doc.getElementsByTagName("error");
			if(errorList.getLength()!=0){			// error encountered
				
				Node errorNode = errorList.item(0);
				Element errorElement = (Element)errorNode;
				String errorValue = errorElement.getFirstChild().getNodeValue();
				String errorString = "{\"result\":{\"error\":\""+ errorValue + "\"}}";
				out.println(errorString);
			}
			else {			//no error present
				
				//adding name to JSON string
				NodeList nameList = doc.getElementsByTagName("Name");
				Node nameNode = nameList.item(0);
				Element nameElement = (Element)nameNode;
				String nameValue = nameElement.getFirstChild().getNodeValue();
				String nameString = "\"Name\":\""+ nameValue + "\",";
				jsonString+=nameString;
				
				//adding symbol to JSON string
				NodeList symbolList = doc.getElementsByTagName("Symbol");
				Node symbolNode = symbolList.item(0);
				Element symbolElement = (Element)symbolNode;
				String symbolValue = symbolElement.getFirstChild().getNodeValue();
				String symbolString = "\"Symbol\":\""+ symbolValue + "\",";
				jsonString+=symbolString;
				
				//adding quote information to JSON string
				NodeList quoteList = doc.getElementsByTagName("Quote");
				Node quoteNode = quoteList.item(0);
				Element quote = (Element) quoteNode;
				
				String quoteChangeType= "N/A", quoteChange= "N/A", quoteChangeInPercent= "N/A", 
						quoteLastTradePriceOnly= "N/A", quoteOpen= "N/A", quoteYearLow= "N/A", 
						quoteYearHigh= "N/A", quoteVolume= "N/A", quoteOneYearTargetPrice= "N/A", 
						quoteBid= "N/A",quoteDaysLow= "N/A", quoteDaysHigh= "N/A", quoteAsk= "N/A",
						quoteAverageDailyVolume= "N/A", quotePreviousClose= "N/A", quoteMarketCapitalization= "N/A";
				
				if(quote.hasChildNodes()){
					quoteChangeType = quote.getElementsByTagName("ChangeType").item(0).getFirstChild().getNodeValue();
					quoteChange = quote.getElementsByTagName("Change").item(0).getFirstChild().getNodeValue();
					quoteChangeInPercent = quote.getElementsByTagName("ChangeInPercent").item(0).getFirstChild().getNodeValue();
					quoteLastTradePriceOnly = quote.getElementsByTagName("LastTradePriceOnly").item(0).getFirstChild().getNodeValue();
					quoteOpen = quote.getElementsByTagName("Open").item(0).getFirstChild().getNodeValue();
					quoteYearLow = quote.getElementsByTagName("YearLow").item(0).getFirstChild().getNodeValue();
					quoteYearHigh = quote.getElementsByTagName("YearHigh").item(0).getFirstChild().getNodeValue();
					quoteVolume = quote.getElementsByTagName("Volume").item(0).getFirstChild().getNodeValue();
					quoteOneYearTargetPrice = quote.getElementsByTagName("OneYearTargetPrice").item(0).getFirstChild().getNodeValue();
					quoteBid = quote.getElementsByTagName("Bid").item(0).getFirstChild().getNodeValue();
					quoteDaysLow = quote.getElementsByTagName("DaysLow").item(0).getFirstChild().getNodeValue();
					quoteDaysHigh = quote.getElementsByTagName("DaysHigh").item(0).getFirstChild().getNodeValue();
					quoteAsk = quote.getElementsByTagName("Ask").item(0).getFirstChild().getNodeValue();
					quoteAverageDailyVolume = quote.getElementsByTagName("AverageDailyVolume").item(0).getFirstChild().getNodeValue();
					quotePreviousClose = quote.getElementsByTagName("PreviousClose").item(0).getFirstChild().getNodeValue();
					quoteMarketCapitalization = quote.getElementsByTagName("MarketCapitalization").item(0).getFirstChild().getNodeValue();
				}
				
				jsonString+="\"Quote\":{\"ChangeType\":\"" + quoteChangeType + "\","
						+ "\"Change\":\"" + quoteChange + "\","
						+ "\"ChangeInPercent\":\"" + quoteChangeInPercent + "\","
						+ "\"LastTradePriceOnly\":\"" + quoteLastTradePriceOnly + "\","
						+ "\"Open\":\"" + quoteOpen + "\","
						+ "\"YearLow\":\"" + quoteYearLow + "\","
						+ "\"YearHigh\":\"" + quoteYearHigh + "\","
						+ "\"Volume\":\"" + quoteVolume + "\","
						+ "\"OneYearTargetPrice\":\"" + quoteOneYearTargetPrice + "\","
						+ "\"Bid\":\"" + quoteBid + "\","
						+ "\"DaysLow\":\"" + quoteDaysLow + "\","
						+ "\"DaysHigh\":\"" + quoteDaysHigh + "\","
						+ "\"Ask\":\"" + quoteAsk + "\","
						+ "\"AverageDailyVolume\":\"" + quoteAverageDailyVolume + "\","
						+ "\"PreviousClose\":\"" + quotePreviousClose + "\","
						+ "\"MarketCapitalization\":\"" + quoteMarketCapitalization + "\"},";
				
				//adding News information to JSON string
				
				NodeList listOfNewsItems = doc.getElementsByTagName("Item");
				int lengthOfItems = listOfNewsItems.getLength();
				String newsString = "\"News\":{\"Item\":[";
				
				for(int i=0; i<lengthOfItems; i++){
					Node itemNode = listOfNewsItems.item(i);
					if(itemNode.getNodeType() == Node.ELEMENT_NODE){
						Element itemElement = (Element)itemNode;
						
						String title ="N/A", link = "N/A";
						//NodeList titleNode = itemElement.getElementsByTagName("Title");
						title = itemElement.getElementsByTagName("Title").item(0).getFirstChild().getNodeValue();
						
						title = replaceAll(title, "\\", "\\\\");
						title= replaceAll(title, "\"", "\\\"");

														//outPutArray = outPutArray+ "\"Title\": \"" + Title+ "\"" ;
						link = itemElement.getElementsByTagName("Link").item(0).getFirstChild().getNodeValue();
						newsString+= "{\"Link\":\"" + link + "\"," + "\"Title\":\"" + title + "\"}";
						if(i!=lengthOfItems-1)
		        		{
							newsString = newsString + ",";
		        		}
					}
				}
				jsonString+= newsString + "]},";
				
				//adding StockChart to Json String
				NodeList stockChart = doc.getElementsByTagName("StockChartImageURL");
				Node stockChartNode = stockChart.item(0);
				Element stockChartElement = (Element)stockChartNode;
				String stockChartValue = stockChartElement.getFirstChild().getNodeValue();
				String stockChartString = "\"StockChartImageURL\":\""+ stockChartValue + "\"}}";
				jsonString+=stockChartString;
				
				out.println(jsonString);	
			}
        }
        catch(MalformedURLException e)
		{
			String error_string = "{\"result\":{\"error\":\"MalformedURLException\"}}";
			out.println(error_string);
			return;	
		}
		catch(IOException e)
		{
			
			String error_string = "{\"result\":{\"error\":\"IOException\"}}";
			out.println(error_string);
			return;	
		}
		catch(Exception e)
		{
			String error_string = "{\"result\":{\"error\":\"Exception\"}}";
			out.println(error_string);
			return;
		}    
    }
}
