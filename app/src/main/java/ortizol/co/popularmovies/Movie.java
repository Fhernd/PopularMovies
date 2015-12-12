package ortizol.co.popularmovies;

/**
 * Created by John Ortiz Ordoñez on 12/11/2015.
 */
public class Movie {
    private int id;
    private String originalTitle;
    private String language;
    private String title;
    private String posterPath;
    private double popularity;
    private int voteCount;
    private boolean adult;
    private String overview;
    private String releaseDate;
    private double voteAverage;

    public Movie() {
    }

    public Movie(int id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
    }

    public Movie(int id, String title, String posterPath, double voteAverage, String overview, String releaseDate) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public Movie(int id, String originalTitle, String language, String title, String posterPath, double popularity, int voteCount, boolean adult, String overview, String releaseDate, double voteAverage) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.language = language;
        this.title = title;
        this.posterPath = posterPath;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.adult = adult;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }
}
