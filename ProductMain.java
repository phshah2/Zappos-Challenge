import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONArray;  
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;  
import org.json.simple.parser.ParseException;

public class ProductMain {
	static ArrayList<ProductDet[]> arraylist=new ArrayList<ProductDet[]>();
	static ArrayList<Double>sList=new ArrayList<Double>();
	static ProductDet[]Set;
	static ProductDet []temp;
	static double min;
	static int count;
	static String url;
	static ProductDet []pdetails;
	static String limit="20";
	static double max=0.0;
	static double totBud;
	static int pCount;
	
	public static void main(String[] args) throws Exception, IOException, ParseException {
	String product;
	String key="52ddafbe3ee659bad97fcce7c53592916a6bfd73";//user key
		
	BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print("What you want to buy : 1.boots  2.bags 3.clothes");//asks user for a keyword to search
		product=br.readLine();
		System.out.println();
		
		//the  url to hit zappos api and fetch 20 products from the API
		url="http://api.zappos.com/Search?term="+product+"&limit="+limit+"&excludes=[\"brandName\",\"colorId\",\"productUrl\",\"thumbnailImageUrl\",\"percentOff\",\"originalPrice\"]&key=";
		
		getJasonObject(url,key);// gets json object from restai and converts it into ProductDet object. 
		
		System.out.println("The product details are : ");
		
		
		for(int i=0;i<Integer.parseInt(limit);i++){
			System.out.println(Double.parseDouble(pdetails[i].getPrice())+"-----"+pdetails[i].getProductId()+"-----"+pdetails[i].getProductName()+"-----"+pdetails[i].getStyleId());
		}
		
		
		System.out.println();
		try {
			min=Double.parseDouble(pdetails[0].getPrice());
		} catch (Exception e) {
			
			
		}
		try {
			for(int i=0;i<pdetails.length;i++){
				if(min>Double.parseDouble(pdetails[i].getPrice())){
					min=Double.parseDouble(pdetails[i].getPrice());
				}
			}
		} catch (Exception e) {
			
		}
		
		System.out.println("Available products in this category are 20");
		
		do{//The no of product required by the user should be less than the total count
			System.out.println();
			System.out.print("Enter no. of products you want to buy :  ");
			pCount=Integer.parseInt(br.readLine());
			System.out.println();
			if(pCount>Integer.parseInt(limit)||pCount<=0){
				System.out.println("no. of products should be less than "+limit+" and greater than 0");
			}
		}while(pCount>Integer.parseInt(limit)||pCount<=0);
			
			
		do{//The no of product required by the user should be less than the total count
			System.out.print("Enter your budget for above products :  ");
			totBud=Double.parseDouble(br.readLine());
			System.out.println();
			if(totBud<=0){
				System.out.println("Please enter budget greater than zero");
			}
		}while(totBud<=0);
			
			
		
		
		temp=new ProductDet[pCount];
		
		closestMatch(pCount,totBud,Integer.parseInt(limit),0);
		printClosestMatches();
		
	}

	/*gets the  json object and converts it into productDetail class object*/
	public static void getJasonObject(String apiUrl,String apiKey) throws ParseException{
		  try {
			  	pdetails=new ProductDet[Integer.parseInt(limit)];
  			  	String finalUrl=apiUrl+apiKey;
				URL url = new URL(finalUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
		 
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
		 
				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
				JSONParser parser = new JSONParser();
				
				String output;
				output = br.readLine();
				Object obj=parser.parse(output);
				JSONObject jsonObject1 = (JSONObject) obj;
				JSONArray jarray = (JSONArray) jsonObject1.get("results");				
				for(int i=0;i<jarray.size();i++){
					JSONObject jsonObject2=(JSONObject)jarray.get(i);
					//System.out.println((String)jsonObject2.get("price"));
					
					String formatedPrice=formatPrice((String)jsonObject2.get("price"));
					pdetails[i]=new ProductDet();
					
					pdetails[i].setPrice(formatedPrice);
					pdetails[i].setProductId((String)jsonObject2.get("productId"));
					pdetails[i].setProductName((String)jsonObject2.get("productName"));
					pdetails[i].setStyleId((String)jsonObject2.get("styleId"));
					
				}
				
		 
				conn.disconnect();
		 
			  } catch (MalformedURLException e) {
		 
				e.printStackTrace();
		 
			  } catch (IOException e) {
		 
				e.printStackTrace();
			  }

	}
	
	//removes the Dollar sign that is appended to the price.
	static String formatPrice(String price){
		String returnValue;
		returnValue=price.substring(1);
		
		return returnValue;
		
		
	}
	
	//finds the closest match depending upon no. of products to be purchased and the budget.
	static void closestMatch(int no,double tBudget,int totalItems,int i){
		
		if(no>totalItems){
			return;
		}
		
		if(tBudget<0){
			
			return;
		}
		if(tBudget>=0.0 && no==0){
			
			Set=new ProductDet[pCount];
			
			for(int j=0;j<Set.length;j++){
				Set[j]=new ProductDet();
				Set[j].setPrice(temp[j].getPrice());
				Set[j].setProductId(temp[j].getProductId());
				Set[j].setProductName(temp[j].getProductName());
				Set[j].setStyleId(temp[j].getStyleId());
			}
			
			if(max<totBud-tBudget){
				max=totBud-tBudget;
				sList.clear();
				arraylist.clear();
				sList.add(max);
				arraylist.add(Set);
			}
			
			else if(max==totBud-tBudget){
				sList.add(max);
				arraylist.add(Set);
			}
			
			return;
		}
		if(tBudget-Double.parseDouble(pdetails[totalItems-1].getPrice())>=0){
			temp[i]=pdetails[totalItems-1];
			
		}
		closestMatch(no-1, tBudget-Double.parseDouble(pdetails[totalItems-1].getPrice()), totalItems-1, i+1);
		closestMatch(no, tBudget, totalItems-1, i);
		return;
		
		
	}
	
	public static void printClosestMatches(){
		System.out.println();
		if(totBud<min){
			System.out.println("Your budget is too less ");
		}
		System.out.println("The closest price for given budget is:"+max);
		
		System.out.println();
		System.out.println("Closest Combination for budget="+totBud+"  and no. of products="+pCount+" is: "+arraylist.size()+" possible sets");
		System.out.println();
		System.out.println();
		//--------------------------------------------------------------------------------------------------------------------------------------
		for(int i=0;i<arraylist.size();i++){
			ProductDet[]p=arraylist.get(i);
			for(int j=0;j<p.length;j++){
				System.out.println("Price-->"+p[j].getPrice()+", ProductID-->"+p[j].getProductId()+", StyleID-->"+p[j].getStyleId()+", Name-->"+p[j].getProductName());
			}
			System.out.println();
		}
	}

}
	


 
