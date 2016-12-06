package upscaleapps.olympiad.Message;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import upscaleapps.olympiad.R;

public class ConversationRow extends BaseAdapter {

    Context context;
    ArrayList<JSONObject> jsonMessages = new ArrayList<>();

    private static LayoutInflater inflater = null;

    public ConversationRow(Context context, ArrayList<JSONObject> data) {
        this.context = context;
        this.jsonMessages = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return jsonMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return jsonMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.conversations_row, null);
        TextView name      = (TextView) view.findViewById(R.id.userName);
        TextView message   = (TextView) view.findViewById(R.id.userMessage);
        TextView timestamp = (TextView) view.findViewById(R.id.userTime);
        ImageView image    = (ImageView) view.findViewById(R.id.userImage);

        try {
            JSONObject o = jsonMessages.get(i);
            name.setText(o.getString("messageTitle"));
            message.setText(o.getString("messageText"));
            timestamp.setText(DateFormat
                    .format("dd-MM-yyyy (HH:mm:ss)", (Long) o.get("messageTime")));
            String imageURL = o.getString("userImage");
            new getProfileImage(image).execute(imageURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private class getProfileImage extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        getProfileImage(ImageView imageView){
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap b = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                b = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return b;
        }

        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
}
