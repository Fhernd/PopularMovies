package ortizol.co.popularmovies;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by johno on 12/11/2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie>{

    public MovieAdapter(Activity context, List<Movie> movies){
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the Movie object from the ArrayAdapter at the appropiate position:
        Movie movie = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new view object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_detail_item, parent, false);
        }

        ImageView moviePoster = (ImageView) convertView.findViewById(R.id.movie_poster);
        new DownloadMoviePosterTask(moviePoster).execute(movie.getPosterPath());

        TextView txtMovieTitle = (TextView) convertView.findViewById(R.id.movie_title);
        txtMovieTitle.setText(movie.getTitle());

        convertView.setTag(movie.getId());

        return convertView;
    }
}
