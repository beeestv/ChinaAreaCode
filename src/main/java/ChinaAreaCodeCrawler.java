import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @description:
 * @date: 2019-06-18
 */
public class ChinaAreaCodeCrawler {
    private final static String url = "http://www.ccb.com/cn/OtherResource/bankroll/html/code_help.html";
    private final static String savePath = "./全国地区代码表.json";

    public static void main(String[] args) {
        FileWriter fileWriter = null;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements areaElementList = doc.select(".addlist");

            List<Map> areaMapList = new ArrayList<Map>();
            Map<String, Object> areaMap;
            for (Element areaElement : areaElementList) {
                Elements rows = areaElement.select("tr");

                areaMap = new HashMap<String, Object>();
                List<Map> cityList = new ArrayList<Map>();
                Map<String, String> cityMap;
                Iterator<Element> iterator = rows.listIterator();
                if (iterator.hasNext()) iterator.next(); //忽略表头
                while (iterator.hasNext()) {
                    Element cityElement = iterator.next();
                    Elements columns = cityElement.select("td");
                    cityMap = new HashMap<String, String>();
                    cityMap.put("code", columns.get(0).text().trim());
                    cityMap.put("name", columns.get(1).text().trim());
                    cityList.add(cityMap);
                }
                areaMap.put("area", areaElement.select("h3").first().text());
                areaMap.put("cities", cityList);
                areaMapList.add(areaMap);
            }
            File f = new File(savePath);
            if (!f.exists()) {
                if (!f.createNewFile()) {
                    throw new RuntimeException("创建文件失败，请检查文件夹");
                }
            }
            fileWriter = new FileWriter(f);
            new ObjectMapper().writeValue(fileWriter, areaMapList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
