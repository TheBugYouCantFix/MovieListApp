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

  def extractUidFromHeaderAndApply[A](f: UserId => A => ZIO[AppEnv, Error, Unit]): A => ZIO[AppEnv, Error, Unit] =
    arg =>
      ZIO.service[String]
        .flatMap(str => {ZIO.log(s"TOKEN: $str").debug; f(str.toLong.assume[UserIdDescription])(arg)})
        .mapError {
          case e: NumberFormatException => AuthError(s"Invalid user ID: ${e.getMessage}")
          case other => other
        }

  private def _updateUsernameHandler(userId: UserId, newUsername: Username): ZIO[AppEnv, Error, Unit] =
    ZIO.serviceWithZIO[UserRepo](_.updateUsername(
      userId, newUsername
    )).mapError(e => AuthError(e.getMessage))

  def updateUsernameHandler(newUsername: Username): ZIO[AppEnv, Error, Unit] =
    extractUidFromHeaderAndApply[Username](_updateUsernameHandler)(newUsername)

  private def _updatePasswordHandler(userId: UserId)(newPassword: Password): ZIO[AppEnv, Error, Unit] =
    for
      newPasswordHash <- hashPassword(newPassword)
      _ <- ZIO.serviceWithZIO[UserRepo](_.updatePasswordHash(
        userId, newPasswordHash
      )).mapError(e => AuthError(e.getMessage))
    yield ()

  def updatePasswordHandler(newPassword: Password): ZIO[AppEnv, Error, Unit] =
    extractUidFromHeaderAndApply[Password](_updatePasswordHandler)(newPassword)

  private def _deleteUserHandler(userId: UserId): ZIO[AppEnv, Error, Unit] =
    ZIO.serviceWithZIO[UserRepo](_.removeById(userId))
      .mapError(e => AuthError(e.getMessage))

  def deleteUserHandler: Unit => ZIO[AppEnv, Error, Unit] =
    _ => extractUidFromHeaderAndApply[Unit](uid => _ => _deleteUserHandler(uid))(())

