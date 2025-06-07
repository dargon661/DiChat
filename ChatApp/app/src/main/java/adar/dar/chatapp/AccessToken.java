package adar.dar.chatapp;

import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {
    private static final String url="https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken(){
        try{
            String jsonString="{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"dichat-10c0b\",\n" +
                    "  \"private_key_id\": \"99bb1dd3cad5ca79d4ff317125516c4b3576461d\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEugIBADANBgkqhkiG9w0BAQEFAASCBKQwggSgAgEAAoIBAQC2nJwDZD7dNQux\\nXvy8XLd9MDfHxC1K3KZwBKepXE2BAyatEW3s4Jle/I1jcZTRjLXutm4yH52uLt14\\n0s8oiuk4KyhJBu3C5FSyvT5Yqd5dMRAsEOQsPuXe6X4HLTflAbfJbnAPDdeRTOaU\\nrcWTLyFmqr2l4z5Gi9OcTLmBGFtTGPRjuzXVfgoAqHQkXXUTidgzpaAsKyMcHcvm\\nfAa9gPnrtfVsFNxw2g7VNcPyCiYnJcAy0mGIE67KoDsw6aBBGBjDd2A7OFIJkfu2\\nNfP59GZeWTrTuwVpXpr28ItWKKowiaDfHKn0L59HP8ntvJPJ2V2agkrTSe/KvdE8\\nL0k9w6prAgMBAAECgf8yaF395wNzYdvYb7VUSZkHoEfFh7b480T+UZ/2dYREw0SU\\ngZCBKCkuDzOi6mIqIK1tN5kzkjFJ46XptEd66Ygh+R0lthlBCHEDkI76lT7Mer6m\\nwK2Q3naVvIkNchimc9LycjR8ngA4xlbgCRfWM9cEgB0vAuDYmGsmugclm5OaBtD9\\nK5H9aIBZ0KdXUb01JXG6LHgptbMZTyGLrDMD8xusabz+UOy4rUT7b/5MQjUBba6U\\nwEs8xODwHe8xJ0Ec5f/pcmfcc8qktFhWZZUhUEo1e7V7yU+g2Jvq0w3ybMOTlV6T\\njZow5ioN199E3lLlQki+3ooiUYIj/U5QonLbnQECgYEA90mvuLOw5gZ7ZRQzoIlv\\nBGXK+JUWpAepSVCcZEkxYn7VOQ7iGCASF1bRArOkKilNN6NGoC9nfPeOn98ZaYw4\\nExd3yjVQTBChU3WuI7jrjTDlKt9Mp25MUf0Ns+X1ftt+Vl/A0c0zH+23cSq4gjgL\\nqA5GU6BECk1ZuarOYemXS8sCgYEAvQuaXl0MkYeFMYQvk9aYd0f+KzNphc+X51aV\\nsN4JGzSmfU6CwvjdvFoFcoEbs8oIwn0SPvkMYMXNPTgjIAGayMYnffa4+sICz/L5\\nnNXwYicHrkOK4oQpk35afbNLKOmFcvmZ/U46ALob70xokAJo1O19WP+c7XzLh/lx\\ndT42h+ECgYApn26DYUN6REcKyW6zK87S56tVUgrgiyWUYIwiWSuFz6FmHDo0I1nI\\nMeW2VbRhIFiN796YYcJCh4yrHisx19csZ3vTPkwIsZfl613nR13mgv9fyvwlBmy0\\nNCrVR/SdaKaINU/IQIjFbAa/dphd2r8jh1uRAd3R+hkLJlPcqSu9JwKBgDS4mKt6\\nXbf0SHP/vlkHfuu9GygnJWWY+G8e2Y5Czd1VeJz3mUYQq54MqPo/AV/Jl1nOEZmY\\nURhn8Y4CKuGRMP6PLJJXjUz5cFlYFL7ldy2YXu79jS0Q2Jt2oHvv1aUqQvpGm+hl\\nuY1cWdAJyhtnYJ8JQGzilHwy0nwwV7Dv0bthAoGAGacNS8M+qZccJfTW2X2LdbH3\\nNoMHlkEDXEu2tkThDbXmieuJjepXHWQKiA1wWMrwzPb0IbguUo9EE1ydF8p20/oi\\n67HlRylLhBUvEwQ9GAS71GowzdpTDuQu5UBs+scsWZP0yeifohkPKR3F9zfvJ3IF\\nVxRPNCFwLLzq4ALuYVc=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-fbsvc@dichat-10c0b.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"102658666556493887080\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40dichat-10c0b.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";

            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials =GoogleCredentials.fromStream(stream).createScoped(Lists.newArrayList(url));
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();

        } catch (IOException e) {
            Log.e("error: ", "" + e.getMessage());
            return null;
        }
    }
}
