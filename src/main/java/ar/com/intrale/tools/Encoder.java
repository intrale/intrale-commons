package ar.com.intrale.tools;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Singleton;

@Singleton
public class Encoder {
	
	public String encode(String text) {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		
		String temporaryPasswordEncrypted = null;
		
		KeySpec spec = new PBEKeySpec(text.toCharArray(), salt, 65536, 128);
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = factory.generateSecret(spec).getEncoded();
			temporaryPasswordEncrypted = new String(hash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return temporaryPasswordEncrypted;
	}
	
	public Boolean match(String text, String encoded) {
		return encoded.equals(this.encode(text));
	}

}
