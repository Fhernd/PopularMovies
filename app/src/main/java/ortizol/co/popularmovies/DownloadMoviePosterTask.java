package ortizol.co.popularmovies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by johno on 12/11/2015.
 */
public class DownloadMoviePosterTask extends AsyncTask<String, Void, Bitmap> {

    /**
     * Represents the image to populate.
     */
    private ImageView ivwMoviePoster;

    public DownloadMoviePosterTask(ImageView ivwMoviePoster) {
        this.ivwMoviePoster = ivwMoviePoster;
    }

    @Override
    protected Bitmap doInBackground(String... imageNames) {
        String imageName = imageNames[0].substring(1);
        String url = "https://image.tmdb.org/t/p/w185/" + imageName;
        Bitmap tempImage = null;
        try{
            InputStream in = new URL(url).openStream();
            tempImage = BitmapFactory.decodeStream(in);
        } catch (Exception e){
            e.printStackTrace();
        }

        return tempImage;
    }

    protected void onPostExecute(Bitmap result){
        ivwMoviePoster.setImageBitmap(result);
    }
}