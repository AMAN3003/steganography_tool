import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;

// stores a symmetric key, and 2 secure randoms
public class Key_Struct_Package {

    public SecureRandom rand;
    public IvParameterSpec ivParam;
    public SecretKeySpec key;
    
    Key_Struct_Package(){}
    
    /**
     * Zeroizes all sensitive data.
     */
    public void make_zero() {
        
    }
}