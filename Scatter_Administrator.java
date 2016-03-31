import java.awt.Point;
import java.security.NoSuchAlgorithmException;
import java.awt.Dimension;
import java.security.SecureRandom;
import java.security.NoSuchProviderException;


public class Scatter_Administrator {
    
    public static final double MIN_ARRAY_LOAD = 0.60;
    
    private SecureRandom _secure_random_;
    
    private final Dimension _dimension;
    
    private Shrink_Array<Point> _shrink_array;

    private boolean in_operational_state;
    
    private boolean is_primer_provided;
    
    private Point[] primer;
    
    private final int _total_points;
    
    private int _points_remaining;
    
    // constructor
    public Scatter_Administrator( Dimension _dimension ) throws NoSuchAlgorithmException, NoSuchProviderException {
        if ( _dimension == null ) {
            throw new IllegalArgumentException( "Dimension provided to Scatter_Administrator is invalid.");
        }
        
        this._dimension = new Dimension( _dimension.width, _dimension.height ); //Create a deep copy of the input
        this.in_operational_state = false;
        this.is_primer_provided = false;
                
        int x = this._dimension.width;
        int y = this._dimension.height;
        this._total_points = x * y;
        this._points_remaining = this._total_points;
        
        if ( x < 1 || y < 1 ) {
            throw new IllegalArgumentException( "Dimension provided has width or height of zero or less. Width: " + x + ". Height: " + y + ".");
        }
        
        //Initialize the SecureRandom using only image dimensions -- this is for getting the primer points
        this._secure_random_ = SecureRandom.getInstance( "SHA1PRNG", "SUN" );
        this._secure_random_.setSeed( (long) ( x * 10000000 ) + y  ); //Shift x to the left.
        
        //Done to intiliaze shrinking array
        Point[] _temp_array = new Point[ x * y ];
        int counter = 0;
        for( int i = 0; i < x; i++ ) {
            for( int j = 0; j < y; j++ ) {
                _temp_array[counter] = new Point( i, j );
                counter++;
            }
        }
       
        this._shrink_array = new Shrink_Array<Point>( _temp_array, MIN_ARRAY_LOAD );
    }
    
    // returns array of points that caller method can use as a nonce
    // always returns same points no matter how many times it is called
    public synchronized Point[] fetch_primer_points( int number ) throws Weird_Exception {
        Point[] result = null;
        
        if ( this.is_primer_provided == true ) {
            result = new Point[ this.primer.length ];
            
            for( int i = 0; i < this.primer.length; i++ ) {
                result[i] = this.primer[i];
            }
        
        } else if ( number <= 0 ) {
            throw new IllegalArgumentException( "Number given to method fetch primer points in scatter Administrator was zero or less." 
                                                + "  This method works only for positive numbers." );
        } else if ( number > ( this._dimension.height * this._dimension.width ) ) {
            throw new IllegalArgumentException( "Number given to method fetch primer points in scatter Administrator was too large.  " 
                    + number + "  primer points were requested but image has a size less than this " + 
                     "." );
        } else if ( this.in_operational_state == true ) {
            throw new UnsupportedOperationException( "Scatter Manager cannot provide primer points while in operational state." );
        } else {
            result = new Point[ number ];
            this.is_primer_provided = true;
            for( int i = 0; i < result.length; i++ ) {
                result[i] = get_next_point_private();
            }
        }
        
        return result;

    }

    // sets securerandom for scatter adminstrator.
    public synchronized boolean secure_random_set( SecureRandom _secure_random_ ) {
        boolean result = false;
        if ( this.in_operational_state == false ) {
            if ( _secure_random_ != null ) {
                this._secure_random_ = _secure_random_;
                this.in_operational_state = true;
                result = true;
            }
        }
        return result;
    }

    // returns array of next points to read/write from
    public synchronized Point[] fetch_next_point( int num_of_points ) throws Weird_Exception {
        
        if ( num_of_points <= 0 ) {
            throw new IllegalArgumentException( "The number of points provided to ScatterManger.fetch_next_point(int) was not positive: " +
                                                num_of_points + "." );
        }
        
        Point[] _temp_array = new Point[ num_of_points ];
        
        for ( int i = 0; i < num_of_points; i++ ) {
            _temp_array[i] = this.fetch_next_point();
        }
        
        return _temp_array;
        
    }
    
    // returns the next point to write/read from
    public synchronized Point fetch_next_point() throws Weird_Exception {
        
        if ( this.in_operational_state == false ) {
            throw new UnsupportedOperationException( "Scatter Administrator is not in operating state." +
                                                    "Hence cannot return the point" );
        }
        
        return this.get_next_point_private();
        
    }
    

    // returns the next point selected at random and removes it from the array
    private synchronized Point get_next_point_private() throws Weird_Exception {
        if ( ( this._shrink_array.Number_Of_Elements() == 0 ) || ( this._points_remaining == 0 ) ) {
            throw new Weird_Exception( "Scatter Administrator doesnot have any more points to return" );
        }
        this._points_remaining--;
        
        int index; //stores random number
        Point _temp_point = null;  //stores result
        int attempts_counter = 0;  //stores number of attempts to get a point to prevent infinite looping
        
        try {
            while( _temp_point == null ) {    
                index = this._secure_random_.nextInt( this._shrink_array.Active_Range() );
                _temp_point = this._shrink_array.remove( index );
                
                attempts_counter++;
                if ( attempts_counter >= 1000000 ) { 
                    throw new Weird_Exception( "Scatter Administrator has attempted a million times to fetch required point.\n" +
                                             "Aborting Process else program will go in infinite loop" );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new Weird_Exception( "An error occurred within Scatter Administrator", e );
        }
        
        return _temp_point;
    }
    
   
    // returns number of points remaining
    public synchronized int num_of_points_remaining() {
        return this._points_remaining;
    }
    
    
    // return number of points used
    public synchronized int no_of_points_used() {
        return ( this._total_points - this._points_remaining );
    }
    

    // returns total points available
    public synchronized int _total_points() {
        return this._total_points;
    }
    
    public synchronized boolean has_started() {
        return this.in_operational_state;
    }
    
    public synchronized boolean Is_pRimer_provided() {
        return this.is_primer_provided;
    }
    
    public synchronized boolean make_zero() {
        return false;
    }
}