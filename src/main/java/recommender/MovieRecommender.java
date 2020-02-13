package recommender;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;

/** recommender.MovieRecommender. A class that is responsible for:
 - Reading movie and ratings data from the file and loading it in the data structure recommender.UsersList.
 *  - Computing movie recommendations for a given user and printing them to a file.
 *  - Computing movie "anti-recommendations" for a given user and printing them to file.
 *  Fill in code in methods of this class.
 *  Do not modify signatures of methods.
 */
public class MovieRecommender {
    private UsersList usersData; // linked list of users
    private HashMap<Integer, String> movieMap; // maps each movieId to the movie title

    public MovieRecommender() {
        movieMap = new HashMap<>();
        usersData = new UsersList();
    }

    /**
     * Read user ratings from the file and save data for each user in this list.
     * For each user, the ratings list will be sorted by rating (from largest to
     * smallest).
     * @param movieFilename name of the file with movie info
     * @param ratingsFilename name of the file with ratings info
     */
    public void loadData(String movieFilename, String ratingsFilename) {

        loadMovies(movieFilename);
        loadRatings(ratingsFilename);
    }

    /** Load information about movie ids and titles from the given file.
     *  Store information in a hashmap that maps each movie id to a movie title
     *
     * @param movieFilename csv file that contains movie information.
     *
     */
    private void loadMovies(String movieFilename) {
        // FILL IN CODE
        try(BufferedReader file = new BufferedReader(new FileReader(movieFilename))){
            String line = file.readLine(), movieName = "";
            String[] holdLine;
            while((line = file.readLine()) != null){
                holdLine = line.split(",");
                if(holdLine[1].contains("\"")){
                    movieName += holdLine[1].substring(1) + ",";
                    for(int count = 2;count < holdLine.length;count++){
                        if((holdLine[count].charAt(holdLine[count].length()-1) + "").equals("\"")){
                            movieName += holdLine[count].substring(0,holdLine[count].length()-1);
                            movieMap.put(Integer.parseInt(holdLine[0]),movieName);
                            movieName = "";
                            break;
                        }
                        movieName += holdLine[count] + ",";
                    }
                }else{
                    movieMap.put(Integer.parseInt(holdLine[0]), holdLine[1]);
                }

            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Load users' movie ratings from the file into recommender.UsersList
     * @param ratingsFilename name of the file that contains ratings
     */
    private void loadRatings(String ratingsFilename) {
        // FILL IN CODE
        try(BufferedReader file = new BufferedReader(new FileReader(ratingsFilename))){
            String line = file.readLine();
            String[] holdLine;
            while((line = file.readLine()) != null){
                holdLine = line.split(",");
                usersData.insert(Integer.parseInt(holdLine[0]), Integer.parseInt(holdLine[1]),
                        Double.parseDouble(holdLine[2]));

            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * * Computes up to num movie recommendations for the user with the given user
     * id and prints these movie titles to the given file. First calls
     * findMostSimilarUser and then getFavoriteMovies(num) method on the
     * "most similar user" to get up to num recommendations. Prints movies that
     * the user with the given userId has not seen yet.
     * @param userid id of the user
     * @param num max number of recommendations
     * @param filename name of the file where to output recommended movie titles
     *                 Format of the file: one movie title per each line
     */
    public void findRecommendations(int userid, int num, String filename) {

        // compute similarity between userid and all the other users
        // find the most similar user and recommend movies that the most similar
        // user rated as 5.
        // Recommend only the movies that userid has not seen (has not
        // rated).
        // FILL IN CODE
        int[] currentUserRate = usersData.get(userid).getFavoriteMovies(num*num);
        int[] similarUserRate = usersData.findMostSimilarUser(userid).getFavoriteMovies(num*num);

        try(PrintWriter file = new PrintWriter(filename)){
            boolean inList = false;
            addLoop:
            for(int simCount = 0;simCount<similarUserRate.length;simCount++){
                for(int currentCount = 0;currentCount<currentUserRate.length;currentCount++){
                    if(similarUserRate[simCount] == currentUserRate[currentCount]){
                        inList = true;
                        break;
                    }
                }
                if(!inList){

                    file.println(movieMap.get(similarUserRate[simCount]));
                    num--;
                }
                if(num == 0){
                    break addLoop;
                }
                inList = false;
            }
            file.flush();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * Computes up to num movie anti-recommendations for the user with the given
     * user id and prints these movie titles to the given file. These are the
     * movies the user should avoid. First calls findMostSimilarUser and then
     * getLeastFavoriteMovies(num) method on the "most similar user" to get up
     * to num movies the most similar user strongly disliked. Prints only
     * those movies to the file that the user with the given userid has not seen yet.
     * Format: one movie title per each line
     * @param userid id of the user
     * @param num max number of anti-recommendations
     * @param filename name of the file where to output anti-recommendations (movie titles)
     */
    public void findAntiRecommendations(int userid, int num, String filename) {

        // compute similarity between userid and all the other users
        // find the most similar user and anti-recommend movies that the most similar
        // user rated as 1.
        // Anti-recommend only the movies that userid has not seen (has not
        // rated).
        // FILL IN CODE
        RatingsList currentRated = usersData.get(userid).getMovieRatings(),
                simRated = usersData.findMostSimilarUser(userid).getMovieRatings();
        int[] currentUserRate = usersData.get(userid).getLeastFavoriteMovies(num),
                simUserRate = usersData.findMostSimilarUser(userid).getLeastFavoriteMovies(num);

        try(PrintWriter file = new PrintWriter(filename)){
            boolean inList = false;
            addLoop:
            for(int simCount = 0;simCount<simUserRate.length;simCount++){
                if(currentRated.getRating(simUserRate[simCount]) == -1 &&
                        simRated.getRating(simUserRate[simCount]) == 1.0){
                    file.println(movieMap.get(simUserRate[simCount]));
                    num--;
                }


                if(num == 0){
                    break addLoop;
                }
                inList = false;
            }


            file.flush();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

}