package org.example.jwtdemo.security.util

import org.example.jwtdemo.model.Fingerprint
import java.security.MessageDigest

fun generateRandomFingerprint(): Fingerprint {
    val random = java.util.Random()
    val raw = ByteArray(32)
    random.nextBytes(raw)
    val md = MessageDigest.getInstance("SHA-256")
    val hash = md.digest(raw).joinToString("") { "%02x".format(it) }
    return Fingerprint(raw.joinToString("") { "%02x".format(it) }, hash)
}

fun Fingerprint.isValid() : Boolean {
    val md = MessageDigest.getInstance("SHA-256")
    val rawBytes = raw.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    val hashBytes = hash.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    val calculatedHash = md.digest(rawBytes)
    return calculatedHash.contentEquals(hashBytes)
}