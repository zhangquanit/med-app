package com.medlinker.debugtools.fun.lane;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.medlinker.debugtools.DTModule;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DTLaneStorage {

    private static final String LANE_FILE_PATH = DTModule.app().getDir("dt", Context.MODE_PRIVATE).getAbsolutePath();
    private static final String LANE_FILE_NAME = "lane";
    private volatile static LaneData mLaneData;

    public static void save(String laneDomains, String laneName) throws Throwable {
        writeFile(laneDomains,laneName);
    }

    public static String getLaneName() {
        try {
            return getLaneData().getLaneName();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return "";
    }

    public static String getLaneDomains() {
        try {
            return getLaneData().getLaneDomains();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return "";
    }

    public static LaneData getLaneData() {
        if (null == mLaneData) {
            String data;
            try {
                data = parseFile();
                // 如果文件不存在，或者没有数据，那么这时候是qa环境，下一次没有必要去读文件
                if (TextUtils.isEmpty(data)) {
                    mLaneData = new LaneData();
                }else {
                    JSONObject object = new JSONObject(data);
                    mLaneData = new LaneData(object.optString("name"), object.optString("laneDomains"));
                }
            }  catch (Exception throwable) {
                Toast.makeText(DTModule.app(),"读取泳道数据失败!",Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        }
        return mLaneData;
    }

    public static void reset() {
        mLaneData = null;
        delete();
    }

    private synchronized static void writeFile(String laneDomains, String laneName) throws Exception {
        JSONObject object = new JSONObject();
        object.put("name", laneName);
        object.put("laneDomains", laneDomains);
        String data = object.toString();
        if (TextUtils.isEmpty(data)) {
            return;
        }
        BufferedWriter writer;
        File laneFile = new File(LANE_FILE_PATH,LANE_FILE_NAME);
        if (!laneFile.exists()) {
            boolean re = laneFile.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(laneFile);
        writer = new BufferedWriter(fileWriter);
        writer.write(data);
        writer.close();
        mLaneData = new LaneData(laneName, laneDomains);
    }

    private static String parseFile() throws IOException {
        File laneFile = new File(LANE_FILE_PATH,LANE_FILE_NAME);
        if (!laneFile.exists() || !laneFile.isFile()) {
            return null;
        }
        FileReader fileReader = new FileReader(laneFile);
        BufferedReader reader = new BufferedReader(fileReader);
        String str;
        StringBuilder data = new StringBuilder();
        while((str = reader.readLine()) != null){
            data.append(str);
        }
        reader.close();
        return data.toString();
    }

    private static boolean delete(){
        File laneFile = new File(LANE_FILE_PATH,LANE_FILE_NAME);
        if (!laneFile.exists() || !laneFile.isFile()) {
            return true;
        }
        return laneFile.delete();
    }

    public static class LaneData {
        LaneData() {
        }

        LaneData(String laneDomains, String laneName) {
            this.laneData = laneDomains;
            this.laneName = laneName;
        }

        private volatile String laneData;
        private volatile String laneName;

        public String getLaneDomains() {
            return laneData;
        }

        public String getLaneName() {
            return laneName;
        }

    }
}
