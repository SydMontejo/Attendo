package com.example.attendo.screen

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.*
import javax.mail.internet.*

object CorreoHelper {
    suspend fun sendEmail(email: String, subject: String, body: String) {
        withContext(Dispatchers.IO) {
            val from = "tu_correo@gmail.com" // Tu correo
            val to = email // Correo destinatario
            val properties = Properties().apply {
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.socketFactory.port", "465")
                put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                put("mail.smtp.auth", "true")
                put("mail.smtp.port", "465")
            }
            val session = Session.getDefaultInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication("tu_correo@gmail.com", "tu_contraseña") // Tu correo y contraseña
                }
            })

            try {
                val mimeMessage = MimeMessage(session).apply {
                    setFrom(InternetAddress(from))
                    addRecipient(Message.RecipientType.TO, InternetAddress(to))
                    setSubject(subject)
                    setText(body)
                }

                // Enviar correo
                Transport.send(mimeMessage)
                println("Correo enviado exitosamente a $email")
            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
    }
}


