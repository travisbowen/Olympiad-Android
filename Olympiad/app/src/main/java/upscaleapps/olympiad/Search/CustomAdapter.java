package upscaleapps.olympiad.Search;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import upscaleapps.olympiad.R;


public class CustomAdapter extends BaseAdapter{


    private Context mContext;
    private List<UserObject>mUserList;


    public CustomAdapter(Context mContext, List<UserObject> mUserList) {
        this.mContext = mContext;
        this.mUserList = mUserList;
    }


    @Override
    public int getCount() {
        return mUserList.size();
    }


    @Override
    public Object getItem(int position) {
        return mUserList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.row_item, null);
        ImageView userImage = (ImageView) view.findViewById(R.id.userImage);
        TextView userName = (TextView) view.findViewById(R.id.userNameTV);
        TextView userInfo = (TextView) view.findViewById(R.id.userInfoTV);
        TextView userLocation = (TextView) view.findViewById(R.id.userLocationTV);
        TextView userDistance = (TextView) view.findViewById(R.id.userDistanceTV);

        String imageUrl = mUserList.get(position).getImage();
        new getProfileImage(userImage).execute(imageUrl);

        userName.setText(mUserList.get(position).getName());
        userInfo.setText(mUserList.get(position).getGender() + " - " + mUserList.get(position).getAge());
        userLocation.setText(mUserList.get(position).getLocation());
        String distance = String.format("%.2f", mUserList.get(position).getDistance());
        userDistance.setText(distance + " miles");

        return view;
    }


    public class getProfileImage extends AsyncTask<String,Void,Bitmap> {

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