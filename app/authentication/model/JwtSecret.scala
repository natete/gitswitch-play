package authentication.model

import com.nimbusds.jose.crypto.{MACSigner, MACVerifier}

/**
  * Created by natete on 15/04/17.
  */
case class JwtSecret(secret: String) {
  val signer = new MACSigner(secret)
  val verifier = new MACVerifier(secret)
}
