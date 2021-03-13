package org.jeecg.modules.test;


import com.google.gson.JsonObject;
import okhttp3.WebSocket;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QuartzTest implements Job{


    @Resource
    private WebSocket webSocket;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String printTime = new SimpleDateFormat("yy-MM-dd HH-mm-ss").format(new Date());
        System.out.println("PrintWordsJob start at:" + printTime + ", prints: Hello Job-" + new Date());

    }
    public void test() throws Exception{
        JSONObject obj = new JSONObject();
        obj.put("1","1");
        webSocket.send(obj.toString());

    }
}