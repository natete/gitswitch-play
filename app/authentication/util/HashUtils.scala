package authentication.util

import java.security.MessageDigest

/**
  * Created by natete on 15/04/17.
  */
object HashUtils {
  def encodePassword(password: String): Array[Byte] =
    MessageDigest.getInstance("SHA-512").digest(password.getBytes("UTF-8"))
}
