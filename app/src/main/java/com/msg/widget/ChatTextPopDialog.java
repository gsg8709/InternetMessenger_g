package com.msg.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import com.feihong.msg.sms.R;
/**
 * 
 * @ClassName: ChatTextPopDialog 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author gengsong@zhongsou.com
 * @date 2014年7月31日 下午8:40:44 
 * @version 3.5.2
 */

public class ChatTextPopDialog extends Dialog {
    public ChatTextPopDialog(Context context) {
        super(context);
        setOwnerActivity((Activity)context);
    }

    public ChatTextPopDialog(Context context, int theme) {
        super(context, theme);
        setOwnerActivity((Activity)context);
    }
    
    public static class Builder {
        private Context context;
        private Bitmap mBitmap;

        public Builder(Context context) {
            this.context = context;
        }
        
        public Builder setContentView(Bitmap mBitmap) {
            this.mBitmap = mBitmap;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public ChatTextPopDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ChatTextPopDialog dialog = new ChatTextPopDialog(context, android.R.style.Animation_Dialog);
            View layout = inflater.inflate(R.layout.pop_dialog_layout, null);
            ImageView im_chat_text_pop=(ImageView) layout.findViewById(R.id.find_imageview);
            if(mBitmap != null) {
            	im_chat_text_pop.setImageBitmap(mBitmap);
            }
            im_chat_text_pop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            return dialog;
        }

    }
    
}
