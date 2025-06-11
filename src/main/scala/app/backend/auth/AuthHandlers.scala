package app.backend.auth

import zio.*
import com.github.roundrop.bcrypt.*
import io.github.iltotore.iron.assume

import app.backend.data.repositories.UserRepo
import app.backend.auth.jwt.JwtService
import app.domain.*
import app.backend.AppEnv
import app.utils.given

object AuthHandlers:
  private def hashPassword(password: Password): ZIO[Any, PasswordHashingFailedError, String] =
    ZIO
      .fromTry(password.bcrypt())
      .mapError(e => PasswordHashingFailedError(e.getMessage))

  private def generateToken(userId: UserId): ZIO[JwtService, AuthError, String] =
    ZIO.serviceWithZIO[JwtService](
      _.jwtEncode(userId)
    ).mapError(e => AuthError(e.getMessage))

  private def isPasswordCorrect(credentials: Credentials): ZIO[UserRepo, Error, Boolean] =
    for
      maybePassword <- ZIO.serviceWithZIO[UserRepo]
        (_.getPasswordHashByUsername(credentials.username))
        .mapError(e => AuthError(e.getMessage))

      res <- maybePassword match
        case Some(passwordHash: String) => ZIO.fromTry(
          credentials.password.isBcrypted(passwordHash)
        ).mapError(e => AuthError(e.getMessage))
        case None => ZIO.succeed(false)
    yield res

  private def executeIfPasswordCorrect[A, B](credentials: Credentials, arg: A, f: A => ZIO[AppEnv, Error, B]): ZIO[AppEnv, Error, B] =
    for
      ips <- isPasswordCorrect(credentials)
      res <- if ips then f(arg) else ZIO.fail(InvalidCredentialsError())
    yield res


  private def maybeUidFromUsername(username: Username): ZIO[AppEnv, Error, Option[UserId]] =
    for
      maybeUid <- ZIO.serviceWithZIO[UserRepo](_.getUidByUsername(
        username,
      )).mapError(e => AuthError(e.getMessage))
    yield maybeUid

  def loginHandler(credentials: Credentials): ZIO[AppEnv, Error, String] =
    for
      maybeUid <- maybeUidFromUsername(credentials.username)
      res <- maybeUid match
        case Some(uid) => executeIfPasswordCorrect[UserId, String](credentials, uid, generateToken)
        case None => ZIO.fail(AuthError(""))
    yield res


  def signupHandler(credentials: Credentials): ZIO[AppEnv, Error, String] =
    for
      passwordHash <- hashPassword(credentials.password)

      uid <- ZIO.serviceWithZIO[UserRepo](_.add(
        User(credentials.username, passwordHash)
      )).mapError(e => AuthError(e.getMessage))

      token <- generateToken(uid)
    yield token

  def authenticateUser(token: String): ZIO[JwtService, Error, UserId] = 
    for 
      jwtService <- ZIO.service[JwtService]
      claim <- jwtService.jwtDecode(token).mapError(_ => AuthError("Invalid token"))
      subject <- ZIO.fromOption(claim.subject).orElseFail(AuthError("Missing subject"))
      userId <- ZIO.attempt(subject.toLong.assume[UserIdDescription])
        .mapError(_ => AuthError("Invalid user ID"))
    yield userId
  

  def updateUsernameHandler(userId: UserId): Username => ZIO[AppEnv, Error, Unit] = 
    newUsername =>
      ZIO.serviceWithZIO[UserRepo](_.updateUsername(userId, newUsername))
        .mapError(e => AuthError(e.getMessage))

  def updatePasswordHandler(userId: UserId): Password => ZIO[AppEnv, Error, Unit] = 
    newPassword => 
      for
        newPasswordHash <- hashPassword(newPassword)
        _ <- ZIO.serviceWithZIO[UserRepo](_.updatePasswordHash(
          userId, newPasswordHash
        )).mapError(e => AuthError(e.getMessage))
      yield ()
  

  def deleteUserHandler(userId: UserId): Unit => ZIO[AppEnv, Error, Unit] = 
    _ => 
    ZIO.serviceWithZIO[UserRepo](_.removeById(userId))
      .mapError(e => AuthError(e.getMessage))
  

