package ortizol.co.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private MovieAdapter mMovieAdapter;

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events:
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView movies = (ListView) rootView.findViewById(R.id.listview_movies);

        // Event which is triggered when the user clicks a list's element:
        movies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int movieId = (Integer) view.getTag();

                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra("MOVIE_ID", movieId);
                startActivity(intent);
            }
        });

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        // Set the adapter for the current fragment's view:
        movies.setAdapter(mMovieAdapter);

        Log.v(LOG_TAG, "onCreate method");

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesData();
    }

    private void updateMoviesData(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String moviesSortOrder = sharedPreferences.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_default));
        new FetchMoviesTask().execute(moviesSortOrder);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]>{

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block:
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a String:
            String moviesJSONStr = null;

            try{
                // Get the user's preferred sort order for the movies:
                String moviesSortOrder = params[0];

                // Construct the URL for the TheMovieDb API page, at
                // https://api.themoviedb.org/:
                Uri.Builder moviesUri = new Uri.Builder();
                moviesUri.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(moviesSortOrder)
                        .appendQueryParameter("api_key", "a158317b8bca8e294cfa0e596aa7fc13");

                URL urlTheMovieDb = new URL(moviesUri.build().toString());

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
                    // But it it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging:
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    // Stream was empty. No point in parsing:
                    return null;
                }

                moviesJSONStr =buffer.toString();

                //Log.v(LOG_TAG, "Forecast JSON String: " + moviesJSONStr);

            } catch (IOException e) {
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

            try {
                return getMoviesDataFromJSON(moviesJSONStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast:
            return null;
        }

        private Movie[] getMoviesDataFromJSON(String moviesDataJSON)
            throws JSONException {

            // These are the names of the JSON objects that need to be extracted:
            final String TMDB_ID = "id";
            final String TMDB_ORIGINAL_TITLE = "original_title";
            final String TMDB_ORIGINAL_LANGUAGE = "original_language";
            final String TDMB_TITLE = "title";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_POPULARITY = "popularity";
            final String TMPB_VOTE_COUNT = "vote_count";
            final String TMPB_ADULT = "adult";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_RELSEASE_DATE = "relsease_date";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_RESULTS = "results";

            JSONObject moviesJson = new JSONObject(moviesDataJSON);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

            Movie[] movies = new Movie[moviesArray.length()];
            Movie movie = null;
            for (int movieNo = 0; movieNo < moviesArray.length(); ++movieNo){

                // Get the JSON object representing the day:
                JSONObject movieJson = moviesArray.getJSONObject(movieNo);

                movie = new Movie(movieJson.getInt(TMDB_ID), movieJson.getString(TMDB_ORIGINAL_TITLE), movieJson.getString(TMDB_POSTER_PATH));

                movies[movieNo] = movie;
            }

//            for(Movie m : movies){
//                Log.v(LOG_TAG, "Movie entry: " + m.getTitle());
//            }

            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null){
                mMovieAdapter.clear();

                for (Movie movie : movies){
                    //Log.v(LOG_TAG, "Movie entry (onPostExecute): " + movie.getTitle());
                    mMovieAdapter.add(movie);
                }
            }
        }
    }
}
