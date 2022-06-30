package com.example.Camerax_demo;
public class ResponseAnalyze {
    public static float[] getResponse(String response) {
        float[] result = {0, 0};
        if(response.contains("\"")) {
            System.out.println("无结果");
            result = null;
        } else {
            String[] data = response.split("\n");
            String x = data[1], y = data[2];
            x = x.split(",")[0];
            x = x.trim();
            y = y.trim();
            result[0] = Float.parseFloat(x);
            result[1] = Float.parseFloat(y);
            System.out.println(x);
            System.out.println(y);
        }
        return result;
    }
}
