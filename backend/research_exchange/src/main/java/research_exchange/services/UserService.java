package research_exchange.services;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;

import io.micronaut.data.exceptions.DataAccessException;
import jakarta.inject.Singleton;
import research_exchange.forms.UserForm;
import research_exchange.models.User;
import research_exchange.repositories.UserRepository;

@Singleton
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean createUser(UserForm userForm) {
        if (userRepository.findByUsername(userForm.getUsername()).isPresent()) {
            return false;
        }

        byte[] salt = generateSalt();
        String hashedPw = hashPassword(userForm.getPassword(), salt);
        User user = new User(userForm.getName(), userForm.getUsername(), userForm.getEmail(), hashedPw, salt,
                "User");

        try {
            userRepository.save(user);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return saltBytes;
    }

    public String hashPassword(String password, byte[] salt) {
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        byte[] hash = null;
        try {
            hash = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        String hashString = Hex.encodeHexString(hash);
        return hashString;
    }

    public boolean verifyPassword(String username, String unhashedPassword, byte[] salt) {
        String hashedPassword = hashPassword(unhashedPassword, salt);

        return hashedPassword.equals(userRepository.findByUsername(username).get().getPassword());
    }

}
