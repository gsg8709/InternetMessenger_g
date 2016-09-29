package com.msg.utils;

import java.util.List;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;
/**
 * @deprecated 发送短信工具类
 * @author gengsong@mainbo.com
 * @time 2016.9.21
 */
public class SendMessage {
    private String SENT_SMS_ACTION = "SENT_SMS_ACTION";  
    private Context context;  
    private Intent sentIntent = new Intent(SENT_SMS_ACTION);  
    private SmsManager smsManager;  
    private PendingIntent sentPI;  
    private String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";  
    private Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);  
    private PendingIntent deliverPI;  
    
    /** 
     * 构造函数 
     * @param c 
     */  
    public SendMessage(Context c){  
        this.context = c;  
        smsManager = SmsManager.getDefault();  
        sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, 0);  
  
  
        //短信发送状态监控  
        context.registerReceiver(new BroadcastReceiver(){  
            @Override  
            public void onReceive(Context context, Intent intent) {  
                switch(getResultCode()){  
                    case Activity.RESULT_OK:  
                    	LogUtil.d("信息已发出");  
                        updateStatus("1");  
                        break;  
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:  
                        Toast.makeText(context, "未指定失败 \n 信息未发出，请重试", Toast.LENGTH_LONG).show();  
                        break;  
                    case SmsManager.RESULT_ERROR_RADIO_OFF:  
                        Toast.makeText(context, "无线连接关闭 \n 信息未发出，请重试", Toast.LENGTH_LONG).show();  
                        break;  
                    case SmsManager.RESULT_ERROR_NULL_PDU:  
                        Toast.makeText(context, "PDU失败 \n 信息未发出，请重试", Toast.LENGTH_LONG).show();  
                        break;  
                }  
  
            }  
        }, new IntentFilter(SENT_SMS_ACTION));  
  
        //短信是否被接收状态监控  
        deliverPI = PendingIntent.getBroadcast(context, 0, deliverIntent, 0);  
        context.registerReceiver(new BroadcastReceiver(){  
  
            @Override  
            public void onReceive(Context context, Intent intent) {  
                // TODO Auto-generated method stub  
                //myDialog.setMessage("已送达服务终端");  
                //checkService();  
            }  
        }, new IntentFilter(DELIVERED_SMS_ACTION));  
  
  
    }  
  
    /** 
     * 发送短信，这里是我需要的几个参数，你可以根据你的具体情况来使用不同的参数 
     * @param mobile 要发送的目标手机号，这个必须要有 
     * @param code 
     * @param msg 发送的短信内容 
     */  
    public void send(String mobile,String msg){ 
        
		if (msg.length() > 70) {

			List<String> divideContents = smsManager.divideMessage(msg);
			for (String text : divideContents) {
				try {
					smsManager.sendTextMessage(mobile, null, text, sentPI,deliverPI);
				} catch (Exception e) {
					Toast.makeText(this.context, "短信发送失败，请检查是系统否限制本应用发送短信",Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		} else {
			smsManager.sendTextMessage(mobile, null, msg, null, null);
		}
        
    }  
  
    private void updateStatus(String status) {  
        //短信发送成功后做什么事情，就自己定吧  
    	Toast.makeText(this.context, "短信发送成功", Toast.LENGTH_SHORT).show();  
    }  
  
  
}  