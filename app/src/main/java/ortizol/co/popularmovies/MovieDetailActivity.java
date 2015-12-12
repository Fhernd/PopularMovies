package ortizol.co.popularmovies;

import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.squareup.picasso.Picasso;

/**
 * Created by johno on 12/11/2015.
 */
public class MovieDetailActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailMovieFragment())
                    .commit();
        }
    }

    public static class DetailMovieFragment extends Fragment{

        private final String MOVIE_ID = "MOVIE_ID";
        private TextView tvwMovieTitle;
        private String posterStr;
        private ImageView ivwMoviePoster;
        private RatingBar rbrMovieRarting;
        private TextView tvwMovieOverview;
        private TextView tvwMovieReleaseDate;

        public DetailMovieFragment(){
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Add this line in order for this segment to handle menu events:
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            Intent intent = getActivity().getIntent();

            if (intent != null && intent.hasExtra(MOVIE_ID)){
                new FetchMovieTask().execute(intent.getIntExtra(MOVIE_ID, 0));
                tvwMovieTitle = (TextView) rootView.findViewById(R.id.movie_detail_title);
                ivwMoviePoster = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
                rbrMovieRarting = (RatingBar) rootView.findViewById(R.id.movie_detail_rating);
                tvwMovieOverview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
                tvwMovieReleaseDate = (TextView) rootView.findViewById(R.id.movie_detail_release_date);
            }

            return rootView;
        }

        public class FetchMovieTask extends AsyncTask<Integer, Void, Movie>{

            private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

            @Override
            protected Movie doInBackground(Integer... params) {
                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block:
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // This will contain the raw JSON as a String:
                String movieJsonStr = null;

                try{
                    // Construct the URL for the TheMovieDb API page, a
                    // https://api.themoviedb.org/:
                    Uri.Builder movieUri = new Uri.Builder();
                    movieUri.scheme("http")
                            .authority("api.themoviedb.org")
                            .appendPath("3")
                            .appendPath("movie")
                            .appendPath(params[0].toString())
                            .appendQueryParameter("api_key", "a158317b8bca8e294cfa0e596aa7fc13");

                    URL urlTheMovieDb = new URL(movieUri.build().toString());

                    // Create the input stream into a String:
                    urlConnection = (HttpURLConnection) urlTheMovieDb.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String:
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    if (inputStream == null){
                        // Nothing to do:
                        return null;
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while((line = reader.readLine()) != null){
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging:
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0){
                        // Stream was empty. No point in parsing:
                        return null;
                    }

                    movieJsonStr = buffer.toString();

                } catch(IOException e){
                    Log.e(LOG_TAG, "Error ", e);

                    return null;
                } finally {
                    if (urlConnection != null){
                        urlConnection.disconnect();
                    }
                    if (reader != null){
                        try{
                            reader.close();
                        } catch (final IOException e){
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                try{
                    return getMovieDataFromJSON(params[0], movieJsonStr);
                } catch (JSONException e){
                    e.printStackTrace();
                }

                // This will only happen if there was an error getting or parsing the forecast:
                return null;
            }

            private Movie getMovieDataFromJSON(int movieId, String movieDataJsonStr)
                    throws JSONException{

                // These are the names of the JSON objects that need to be extracted:
                final String TMDB_ORIGINAL_TITLE = "original_title";
                final String TMDB_POSTER_PATH = "poster_path";
                final String TMDB_VOTE_AVERAGE = "vote_average";
                final String TMDB_OVERVIEW = "overview";
                final String TMDB_RELEASE_DATE = "release_date";

                JSONObject movieJsonObj = new JSONObject(movieDataJsonStr);

                Movie movie = new Movie(movieId, movieJsonObj.getString(TMDB_ORIGINAL_TITLE), movieJsonObj.getString(TMDB_POSTER_PATH),
                                movieJsonObj.getDouble(TMDB_VOTE_AVERAGE), movieJsonObj.getString(TMDB_OVERVIEW), movieJsonObj.getString(TMDB_RELEASE_DATE));

                return movie;
            }

            @Override
            protected void onPostExecute(Movie movie) {
                if (movie != null) {
                    tvwMovieTitle.setText(movie.getTitle());
                    posterStr = movie.getPosterPath();
                }
                Picasso.with(getContext()).load("https://image.tmdb.org/t/p/w185/" + posterStr.substring(1)).into(ivwMoviePoster);
                rbrMovieRarting.setRating(((float) movie.getVoteAverage() / 10.0f) * 5.0f);
                tvwMovieOverview.setText(movie.getOverview());
                tvwMovieReleaseDate.setText(movie.getReleaseDate());
            }
        }
    }
}
