package gpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class GptCall {

    public static String callGPT(int strategyId, String query) {
        log.info("callGPT strategyId: {} querySize: {}", strategyId, query.length());
        // 构造params
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        // 构造整个请求Body
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("strategyId", String.valueOf(strategyId));
        requestMap.put("thirdPartyId", UUID.randomUUID());
        requestMap.put("params", params);
        requestMap.put("bufferUntilSentence", true);

        HttpURLConnection conn = null;
        try {
            // 创建URL对象，并实例化HTTP连接
            // TODO gpt-http接口
            URL url = new URL("TODO");
            conn = (HttpURLConnection) url.openConnection();

            // 设置连接参数
            conn.setConnectTimeout(100000); // 连接超时 单位毫秒
            conn.setReadTimeout(300000); // 读取超时 单位毫秒
            conn.setRequestMethod("POST"); // 设置请求方式
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // 发送请求消息
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(new Gson().toJson(requestMap));
            writer.flush();
            writer.close();
            os.close();

            // 获取响应消息
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            String string = sb.toString();
            String jsonString = string.substring(5);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(jsonString, Map.class);
            String response = (String) map.get("data");
            // 将JSON字符串转换为ResponseEvent对象
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect(); // 断开连接
            }
        }
        return null;
    }



}
