public class Shrink_Array<T extends Object> {
    
    private Object[] array;
    
    private final double minimum_load_factor_;
    
    private int number_of_elements;
    
    private int active_range;
    
    private final int original_size;
    

    // constructor for initialising private variables
    // minimum load factor is in range (0,1) expectedly b/w 0.5 or 0.6
    public Shrink_Array( T[] original_array, double minimum_load_factor ) {
        if ( minimum_load_factor >= 1 || minimum_load_factor <= 0 ) {
            throw new IllegalArgumentException("Load factor minimum value given to class shrink array should be " + 
                                                "inside the range (0,1) " );
        }
        this.array = new Object[original_array.length];
        this.minimum_load_factor_ = minimum_load_factor;
        this.number_of_elements = this.array.length;
        this.active_range = this.array.length;
        
        for( int i = 0; i < this.array.length; i++ ) {
            //count number of null elements in the original array
            T _temp_ = null;
            if ( original_array[i] == null ) {
                this.number_of_elements--;
            } else {
                try {
                    _temp_ = (T) original_array[i];
                } catch ( Exception e ) {
                    throw new IllegalArgumentException( "Invalid object given in input array in shrink array class " +
                                                        "Object is not of Type T.\nObject: " + original_array[i] );
                }
            }
            
            this.array[i] = _temp_;   //Just a way to force it to check that the inputs are of Type T during the constructor
        }
        
        //shrink array by removing null elements
        if ( this.number_of_elements < this.array.length ) {
            this.shrink();
        }
        
        this.original_size = this.number_of_elements;
        
        if ( this.number_of_elements == 0 ) {
            throw new IllegalArgumentException( "The array provided to the constructor of Shrink_Array was either empty or " + 
                                                "completely consisted of null entries." );
        }
        
    }
    

    public synchronized int Number_Of_Elements() {
        return this.number_of_elements;
    }
    

    public synchronized int Active_Range() {
        return this.active_range;
    }
    
   
    public synchronized double Current_Load_Factor() {
        return ((double) this.number_of_elements) / ((double) this.active_range);
    }
    
    
    public synchronized double minimum_load_factor() {
        return this.minimum_load_factor_;
    }
    
    
    public synchronized int fetch_Original_Size() {
        return this.original_size;
    }
    

    // returns value at index given as input and remove that element
    public synchronized T remove( int _index_ ) throws IllegalArgumentException, Exception {
                
        T _temp_ = this.peek(_index_);
        
        if ( _temp_ != null ) {
            this.array[_index_] = null;
            this.number_of_elements--;
            
            if ( this.minimum_load_factor_ > this.Current_Load_Factor() ) {
                this.shrink();
            }
        }
        
        return _temp_;
        
    }
    
    
    // returns the value at given index without removing it
    @SuppressWarnings("unchecked")
    public synchronized T peek( int _index_ ) throws IllegalArgumentException {
        if ( _index_ < 0 || _index_ >= this.active_range ) {
            throw new IllegalArgumentException( "The index provided to method peek Shrink Array must be " +
                                                "in the range : " +
                                                "[0," + this.active_range + ") ");
        }
        
        T answer;
        if ( this.array[_index_] == null ) {
            answer = null;
        } else {
            answer = (T)this.array[_index_];
        }
        return answer;
        
    }
    
    /**
     * Private helper function.  Moves non-null values from larger indices to
     * lower indices with null valus.
     * @throws Exception If there is an error when shrinking the array (i.e. numbers
     *                   are not adding up).  The Shrink_Array should not be used if this occurs.
     */

    // replaces non null values at higher indices with null values at lower indices 
    private synchronized void shrink() {
        int temp_lower_value = 0;
        int temp_higher_value = this.active_range - 1;
        
        boolean is_lower_empty;
        boolean is_higher_full;
        
        while( temp_lower_value < temp_higher_value ) {
            is_lower_empty = false;
            is_higher_full = false;
            
            //The lower process counts up until it finds a null value, then it stops.
            // in similar waay higher process
            while( (temp_lower_value < temp_higher_value) && is_lower_empty == false ) {
                if( this.array[temp_lower_value] == null ) {
                    is_lower_empty = true;  //If we've found an empty array _index_, then we stop
                } else {
                    ++temp_lower_value;        //Else, check the next one
                }
            }
            
            if ( is_lower_empty == true ) {
                while( (temp_lower_value < temp_higher_value) && is_higher_full == false ) {
                    if( this.array[temp_higher_value] != null ) {
                        is_higher_full = true;  //If we've found a full array _index_, then we stop
                    } else {
                        --temp_higher_value;       //Else, check the next one
                    }
                }
                
                if ( is_higher_full == true ) {
                    array[temp_lower_value] = array[temp_higher_value];
                    array[temp_higher_value] = null;
                }
                
            }
        }
                
        this.active_range = this.number_of_elements;
        
    }

}