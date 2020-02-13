package recommender; /**
 * recommender.RatingsList.
 * A class that stores movie ratings for a user in a custom singly linked list of
 * recommender.RatingNode objects. Has various methods to manipulate the list. Stores
 * only the head of the list (no tail! no size!). The list should be sorted by
 * rating (from highest to smallest).
 * Fill in code in the methods of this class.
 * Do not modify signatures of methods. Not all methods are needed to compute recommendations,
 * but all methods are required for the assignment.
 */

import java.util.HashMap;
import java.util.Iterator;

public class RatingsList implements Iterable<RatingNode> {

    private RatingNode head;
    // Note: you are *not* allowed to store the tail or the size of this list


    /**
     * Changes the rating for a given movie to newRating. The position of the
     * node within the list should be changed accordingly, so that the list
     * remains sorted by rating (from largest to smallest).
     *
     * @param movieId id of the movie
     * @param newRating new rating of this movie
     */
    public void setRating(int movieId, double newRating) {
        // FILL IN CODE
        RatingNode current = head;
        if(current.getMovieId() == movieId){
            if(current.next() == null){
                head = null;
            }else{

                head = current.next();
            }

        }else {
            movieIdLoop:
            while (current.next() != null) {
                if (current.next().getMovieId() == movieId) {
                    if(current.next().next() == null){
                        current.setNext(null);
                    }else{
                        current.setNext(current.next().next());
                    }
                    break movieIdLoop;
                }
                current = current.next();
            }
        }

        insertByRating(movieId, newRating);

    }

    /**
     * Return the rating for a given movie. If the movie is not in the list,
     * returns -1.
     * @param movieId movie id
     * @return rating of a movie with this movie id
     */
    public double getRating(int movieId) {
        // FILL IN CODE
        RatingNode current = head;
        while(current != null){
            if(current.getMovieId() == movieId){
                return current.getMovieRating();
            }
            current = current.next();
        }
        return -1; // don't forget to change it

    }

    /**
     * Return the head, all movies that are rated, node.
     * @return all movies that are rated
     */
    public RatingNode getHead(){
        return head;
    }


    /**
     * Insert a new node (with a given movie id and a given rating) into the list.
     * Insert it in the right place based on the value of the rating. Assume
     * the list is sorted by the value of ratings, from highest to smallest. The
     * list should remain sorted after this insert operation.
     *
     * @param movieId id of the movie
     * @param rating rating of the movie
     */
    public void insertByRating(int movieId, double rating) {
        // FILL IN CODE. Make sure to test this method thoroughly
        RatingNode current = head, nextCheck = head, hold = new RatingNode(movieId, rating);
        if(head ==null){
            head = hold;
        }else if(head.next() == null ||
                (head.getMovieRating() < rating) ||
                (head.getMovieRating() == rating && head.getMovieId() < movieId)){
            if(head.getMovieRating() < rating){
                hold.setNext(current);
                head = hold;

            }else if(head.getMovieRating() > rating){
                head.setNext(hold);
            }else{
                if(head.getMovieId() > movieId){
                    head.setNext(hold);
                }else{
                    hold.setNext(current);
                    head = hold;
                }
            }
            current = null;
        }
        movieRateLoop:
        while(current != null){
            if(current.next() == null){
                current.setNext(hold);
                break movieRateLoop;

            }
            if(current.next().getMovieRating() == rating){
                nextCheck = current;
                while(nextCheck != null) {
                    if(nextCheck.next() == null){
                        nextCheck.setNext(hold);
                        break movieRateLoop;
                    }else if(nextCheck.next().getMovieId() < movieId ||
                            nextCheck.next().getMovieRating() < rating){

                        hold.setNext(nextCheck.next());
                        nextCheck.setNext(hold);
                        break movieRateLoop;
                    }
                    nextCheck = nextCheck.next();
                }

            }else if(current.next().getMovieRating() < rating){
                hold.setNext(current.next());
                current.setNext(hold);
                break movieRateLoop;
            }
            current = current.next();
        }

    }



    /**
     * Computes similarity between two lists of ratings using Pearson correlation.
     * https://en.wikipedia.org/wiki/Pearson_correlation_coefficient
     * Note: You are allowed to use a HashMap for this method.
     *
     * @param otherList another RatingList
     * @return similarity computed using Pearson correlation
     */
    public double computeSimilarity(RatingsList otherList) {
        double similarity = 0;
        // FILL IN CODE
        RatingNode other = otherList.head, current = head;
        double x = 0, xPow2 = 0, y = 0, yPow2 = 0, xy = 0, matching = 0;
        HashMap<Integer, Double> movies = new HashMap<Integer, Double>();
        while(other != null){
            movies.put(other.getMovieId(), other.getMovieRating());
            other = other.next();
        }

        while(current != null){

            if(movies.get(current.getMovieId()) != null){
                matching++;
                x += current.getMovieRating();
                xPow2 += current.getMovieRating() * current.getMovieRating();
                y += movies.get(current.getMovieId());
                yPow2 += movies.get(current.getMovieId()) * movies.get(current.getMovieId());
                xy += current.getMovieRating() * movies.get(current.getMovieId());
            }
            current = current.next();
        }

        if(Math.sqrt(matching * xPow2 - x * x) * Math.sqrt(matching * yPow2 - y * y) == 0){
            return 0;
        }
        similarity = (matching * xy - x * y)
                    / (Math.sqrt(matching * xPow2 - x * x) * Math.sqrt(matching * yPow2 - y * y));
        return similarity;

    }
    /**
     * Returns a sublist of this list where the rating values are in the range
     * from begRating to endRating, inclusive.
     *
     * @param begRating lower bound for ratings in the resulting list
     * @param endRating upper bound for ratings in the resulting list
     * @return sublist of the recommender.RatingsList that contains only nodes with
     * rating in the given interval
     */
    public RatingsList sublist(int begRating, int endRating) {
        RatingsList res = new RatingsList();

        // FILL IN CODE
        RatingNode current = head;
        while(current != null){
            if(current.getMovieRating() < begRating){
                break;
            }
            if(current.getMovieRating() <= endRating){
                res.insertByRating(current.getMovieId(), current.getMovieRating());
            }
            current = current.next();
        }

        return res;
    }

    /** Traverses the list and prints the ratings list in the following format:
     *  movieId:rating; movieId:rating; movieId:rating;  */
    public void print() {
        // FILL IN CODE
        RatingNode current = head;
        while(current != null){
            System.out.printf("%s:%s; ", current.getMovieId(), current.getMovieRating());
            current = current.next();
        }
        System.out.println();
    }

    /**
     * Returns the middle node in the list - the one half way into the list.
     * Needs to have the running time O(n), and should be done in one pass
     * using slow & fast pointers (as described in class).
     *
     * @return the middle recommender.RatingNode
     */
    public RatingNode getMiddleNode() {

        // FILL IN CODE
        RatingNode current = head, currentDouble = head;
        middleLoop:
        while(currentDouble != null){
            if(currentDouble.next() == null) break middleLoop;
            if(currentDouble.next().next() == null) break middleLoop;
            currentDouble = currentDouble.next().next();
            current = current.next();
        }

        return current; // don't forget to change it
    }

    /**
     * Returns the median rating (the number that is halfway into the sorted
     * list). To compute it, find the middle node and return it's rating. If the
     * middle node is null, return -1.
     *
     * @return rating stored in the node in the middle of the list
     */
    public double getMedianRating() {
        // FILL IN CODE
        RatingNode middle = getMiddleNode();
        return middle.getMovieRating(); // don't forget to change it
    }

    /**
     * Returns a recommender.RatingsList that contains n best rated movies. These are
     * essentially first n movies from the beginning of the list. If the list is
     * shorter than size n, it will return the whole list.
     *
     * @param n the maximum number of movies to return
     * @return recommender.RatingsList containing first n movie ratings
     */
    public RatingsList getNBestRankedMovies(int n) {
        // FILL IN CODE
        RatingsList recommend = new RatingsList();
        RatingNode current = head;
        while(current != null && n > 0){

            recommend.insertByRating(current.getMovieId(), current.getMovieRating());
            n--;
            current = current.next();
        }
        return recommend; // don't forget to change
    }

    /**
     * * Returns a recommender.RatingsList that contains n worst rated movies for this user.
     * Essentially, these are the last n movies from the end of the list.
     * Note: This method should compute the result in one pass. Do not use the size variable.
     * Note: To find the n-th node from the end of the list, use the technique we discussed in class:
     * use two pointers, where first, you move only one pointer so that pointers are n-nodes apart,
     * and then move both pointers together until the first pointer reaches null; when it happens,
     * the second pointer would be pointing at the correct node.
     * Do NOT use reverse(). Do NOT destroy the list.
     *
     * @param n the maximum number of movies to return
     * @return recommender.RatingsList containing n lowest ranked movies (ranked by this user)
     */
    public RatingsList getNWorstRankedMovies(int n) {

        // FILL IN CODE
        RatingsList recommend = new RatingsList();
        RatingNode newHead = head, newTail = head;
        int count = 0;
        while(newTail.next() != null){
            while(newTail.next() != null && count != n-1){
                newTail = newTail.next();
                count++;
            }
            newHead = newHead.next();
            newTail = newTail.next();
        }

        while(newHead != null){ //still considered 1 pass?
            recommend.insertByRating(newHead.getMovieId(), newHead.getMovieRating());
            newHead = newHead.next();
        }
        return recommend; // don't forget to change
    }

    /**
     * Return a new list that is the reverse of the original list. The returned
     * list is sorted from lowest ranked movies to the highest rated movies.
     * Use only one additional recommender.RatingsList (the one you return) and constant amount
     * of memory. You may NOT use arrays, ArrayList and other built-in Java Collections classes.
     * Read description carefully for requirements regarding implementation of this method.
     *
     * @param head head of the RatingList to reverse
     * @return reversed list
     */
    public RatingsList reverse(RatingNode head) {
        RatingsList r = new RatingsList();
        // FILL IN CODE
        RatingNode current = head.next(), newHead = new RatingNode(head.getMovieId(), head.getMovieRating()), hold;
        while(current != null){
            hold = new RatingNode(current.getMovieId(), current.getMovieRating());
            hold.setNext(newHead);
            newHead = hold;
            current = current.next();
        }
        r.head = newHead;

        return r;
    }

    /**
     * Returns an iterator for the list
     * @return iterator
     */
    public Iterator<RatingNode> iterator() {

        return new RatingsListIterator(0);
    }

    // ------------------------------------------------------
    /**
     * Inner class, RatingsListIterator
     * The iterator for the ratings list. Allows iterating over the recommender.RatingNode-s of
     * the list.
     */
    private class RatingsListIterator implements Iterator<RatingNode> {

        // FILL IN CODE: add instance variable(s)
        private RatingNode nextNode;
        /**
         * Creates a new the iterator starting at a given index
         * @param index index
         */
        public RatingsListIterator(int index) {
            // FILL IN CODE
            nextNode = head;
            for(int count = 0;count<index;count++){
                nextNode = nextNode.next();
            }

        }

        /**
         * Checks if there is a "next" element of the list
         * @return true, if there is "next" and false otherwise
         */
        public boolean hasNext() {
            // FILL IN CODE
            return nextNode != null;
        }

        /**
         * Returns the "next" node and advances the iterator
         * @return next node
         */
        public RatingNode next() {
            // FILL IN CODE
            if(!hasNext()){
                System.out.println("No next element");
            }
            RatingNode temp = nextNode;
            nextNode = nextNode.next();
            return temp; // don't forget to change
        }

        public void remove() {
            // No need to implement for this assignment
            throw new UnsupportedOperationException();
        }

    }


}