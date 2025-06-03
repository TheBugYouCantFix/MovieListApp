package app.backend

import app.backend.auth.jwt.JwtService
import zio.*
import com.github.roundrop.bcrypt.bcrypt

import scala.util.{Failure, Success, Try}
import app.backend.data.repositories.UserRepo
import app.domain
import app.domain.Credentials
import zio.*


object AuthHandlers:
  private type AuthEnv = UserRepo & JwtService
  private def hashPassword(password: Password): ZIO[Any, PasswordHashingFailedError, String] =
    ZIO
      .fromTry(password.bcrypt())
      .mapError(e => PasswordHashingFailedError(e.getMessage))
      
  private def generateToken(userId: UserId): ZIO[JwtService, AuthError, String] =
    ZIO.serviceWithZIO[JwtService](
      _.jwtEncode(userId)
    ).mapError(e => AuthError(e.getMessage))
    
  def loginHandler(credentials: Credentials): ZIO[AuthEnv, Error, String] =
    for
      passwordHash <- hashPassword(credentials.password) 

      maybeUid <- ZIO.serviceWithZIO[UserRepo](_.getUidByCredentials(
        credentials.username,
        passwordHash
      )).mapError(e => AuthError(e.getMessage))

      token <- maybeUid match
        case Some(uid: UserId) => generateToken(uid) 
        case None => ZIO.fail(InvalidCredentialsError())

    yield token
    
  def signupHandler(credentials: Credentials): ZIO[AuthEnv, Error, String] = 
    for
      passwordHash <- hashPassword(credentials.password)
      
      uid <- ZIO.serviceWithZIO[UserRepo](_.add(
        User(credentials.username, passwordHash) 
      )).mapError(e => AuthError(e.getMessage))
      
      token <- generateToken(uid)
    yield token 
    