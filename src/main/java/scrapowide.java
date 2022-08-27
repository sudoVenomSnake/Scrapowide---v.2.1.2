import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.awt.event.*;

public class scrapowide
{
    public static ArrayList<String> getCountryData(String country) throws IOException
    {
        country = country.trim().toLowerCase();
        if(country.contains(" "))
        {
            String c = country.substring(0, 1);
            for(int i = 0; i < country.length(); i++)
            {
                if(country.charAt(i) == ' ')
                {
                    c = c + Character.toString(country.charAt(i + 1));
                }
            }
            country = c;
        }
        if(country.equals("usoa"))
        {
            country = "usa";
        }
        String url = "https://www.worldometers.info/coronavirus/country/" + country + "/";
        Document d =  Jsoup.connect(url).get();
        Elements elements = d.select("#maincounter-wrap");
        ArrayList<String> l = new ArrayList<>();
        elements.forEach((e) ->
        {
            String text = e.select("h1").text();
            String count = e.select("span").text();
            if(text.equals("") != true || count.equals("") != true)
            {
                l.add(text + " : " + count);
            }
        });
        return l;
    }
    
    //WikiSearch
    public static String searchWiki(String arg) throws IOException
    {
        arg = arg.trim().replace(" ", "_");
        Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/" + arg).get();
        Elements paragraphs = doc.select(".mw-content-ltr p, .mw-content-ltr li");
        Element p = paragraphs.first();
        int i = 1;
        while(true)
        {
            p = paragraphs.get(i);
            if(p.text() != null && p.text().length() > 200)
            {
                break;
            }
            i++;
        }
        return p.text();
    }
    
    //Accessing weather info using weather api
    public static String[] getWeather(String q) throws IOException, InterruptedException
    {
        q = q.replace("_", " ");
        String url = "http://api.weatherapi.com/v1/current.json?key=2526033fed7e400e98f113407221406&q=" + q + "&aqi=no";
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String info = response.body();
        return jParse(info);
    }
    
    //Accessing json file
    public static String[] jParse(String i) throws IOException
    {
        JSONObject obj1 = new JSONObject(i);
        JSONObject location = obj1.getJSONObject("location");
        String name = location.getString("name");
        String region = location.getString("region");
        String country = location.getString("country");
        JSONObject current = obj1.getJSONObject("current");
        double t = current.getDouble("temp_c");
        JSONObject condition = current.getJSONObject("condition");
        String text = condition.getString("text");
        double precipitation = current.getDouble("precip_mm");
        double wind = current.getDouble("wind_kph");
        String temp = Double.toString(t);
        String p = Double.toString(precipitation);
        String w = Double.toString(wind);
        String full[] = new String[]{"Name - " + name + "<br>Region - " + region + "<br>Condition - " + text + "<br>Temperature - " + temp + "<br>Wind - " + w + "<br>Precipitation - " + p, country};
        return full;
    }
    
    //GUI
    public static void main(String[] args) throws Exception
    {
        Scanner sc = new Scanner(System.in);

        JFrame f=new JFrame("Scrapowide :D");
        JTextField tf=new JTextField();
        tf.setBounds(397,17, 201,20);
        JButton b=new JButton("Scrapowide");
        b.setBounds(432,71,110,26);
        JLabel l0 = new JLabel();
        l0.setBounds(16, 112, 971, 124);
        JLabel l1 = new JLabel();
        l1.setBounds(16, 254, 971, 219);
        JLabel l2 = new JLabel();
        l2.setBounds(16, 483, 971, 85);
        b.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    String city = tf.getText();
                    String w[] = getWeather(city);
                    String wiki = searchWiki(city);
                    System.out.println(wiki);
                    ArrayList<String> l = getCountryData(w[1]);
                    String lol = "Coronavirus Updates Realtime - ";
                    for(String k : l)
                    {
                        lol = lol + "<br>" + k;
                    }
                    wiki = wiki.replace(".", ".<br>");
                    wiki = "<html>" + wiki + "<";
                    w[0] = "<html>" + w[0] + "<";
                    lol = "<html>" + lol + "<";
                    System.out.println(w[0]);
                    System.out.println(lol);
                    l0.setText(wiki);
                    l1.setText(w[0]);
                    l2.setText(lol);
                }
                catch(Exception f)
                {
                    System.out.println(f.getMessage());
                }
            }
        });
        f.add(b);
        f.add(tf);
        f.add(l0);
        f.add(l1);
        f.add(l2);
        f.setSize(1000,600);
        f.setLayout(null);
        f.setVisible(true);
        sc.close();
    }
}
