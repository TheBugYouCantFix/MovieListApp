package app.backend.auth

import zio.*
import com.github.roundrop.bcrypt.*
import io.github.iltotore.iron.assume

import app.backend.data.repositories.UserRepo
import app.backend.auth.jwt.JwtService
import app.domain.*
import app.domain.credentials.*
import app.backend.AppEnv
import app.backend.auth.requestmodels.*

object AuthHandlers:
  import app.domain.credentials.given

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
      userRepo <- ZIO.service[UserRepo]
      maybePassword <- (credentials.identifier match
        case uid: UserId => userRepo.getPasswordHashById(uid)
        case username: Username => userRepo.getPasswordHashByUsername(username)
        ).mapError(e => AuthError(e.getMessage))

      res <- maybePassword match
        case Some(passwordHash: String) => ZIO.fromTry(
          credentials.password.isBcrypted(passwordHash)
        ).mapError(e => AuthError(e.getMessage))
        case None => ZIO.succeed(false)
    yield res

  private def executeIfPasswordCorrect[A, B](credentials: Credentials, arg: A, f: A => ZIO[AppEnv, Error, B]): ZIO[AppEnv, Error, B] =
    for
      ips <- isPasswordCorrect(credentials).debug
      res <- if ips then f(arg) else ZIO.fail(InvalidCredentialsError()).debug
    yield res


  def loginHandler(credentials: PreAuthCredentials): ZIO[AppEnv, Error, String] = {
    for
      maybeUid <- ZIO.serviceWithZIO[UserRepo](_.getUidByUsername(
        credentials.username
      )).mapError(e => AuthError(e.getMessage))

      res <- maybeUid match
        case Some(uid) => executeIfPasswordCorrect[UserId, String](credentials, uid, generateToken)
        case None => ZIO.fail(AuthError(""))
    yield res
  }


  def signupHandler(credentials: PreAuthCredentials): ZIO[AppEnv, Error, String] =
    for
      passwordHash <- hashPassword(credentials.password).debug

      uid <- ZIO.serviceWithZIO[UserRepo](_.add(
        User(credentials.username, passwordHash)
      )).mapError(e => AuthError(e.getMessage)).debug

      token <- generateToken(uid)
      _ <- ZIO.logInfo("signup complete")
    yield token

  def authenticateUser(token: String): ZIO[JwtService, Error, UserId] =
    for
      jwtService <- ZIO.service[JwtService].debug
      claim <- jwtService.jwtDecode(token).mapError(_ => AuthError("Invalid token")).debug
      subject <- ZIO.fromOption(claim.subject).orElseFail(AuthError("Missing subject")).debug
      userId <- ZIO.attempt(subject.toLong.assume[UserIdDescription]).debug
        .mapError(_ => AuthError("Invalid user ID")).debug
      _ <- ZIO.logInfo(s"auth successful. uid: $userId")
    yield userId


  private def _updateUsernameHandler(uuo: UpdateUsernameOperation): ZIO[AppEnv, Error, Unit] =
    ZIO.serviceWithZIO[UserRepo](_.updateUsername(uuo.userId, uuo.newUsername))
      .mapError(e => AuthError(e.getMessage)).debug

  def updateUsernameHandler(userId: UserId): UpdateUsernameRequest => ZIO[AppEnv, Error, Unit] =
    updateUsernameReq =>
      executeIfPasswordCorrect(
        Credentials(userId, updateUsernameReq.password),
        UpdateUsernameOperation(userId, updateUsernameReq.newUsername),
        _updateUsernameHandler
      )

  private def _updatePasswordHandler(upo: UpdatePasswordOperation): ZIO[AppEnv, Error, Unit] =
    for
      newPasswordHash <- hashPassword(upo.newPassword)
      _ <- ZIO.serviceWithZIO[UserRepo](_.updatePasswordHash(
        upo.userId, newPasswordHash
      )).mapError(e => AuthError(e.getMessage))
    yield ()

  def updatePasswordHandler(userId: UserId): UpdatePasswordRequest=> ZIO[AppEnv, Error, Unit] =
    updatePasswordReq =>
      executeIfPasswordCorrect(
        Credentials(userId, updatePasswordReq.password),
        UpdatePasswordOperation(userId, updatePasswordReq.newPassword),
        _updatePasswordHandler
      )

  private def _deleteUserHandler(userId: UserId): ZIO[AppEnv, Error, Unit] =
    ZIO.serviceWithZIO[UserRepo](_.removeById(userId))
      .mapError(e => AuthError(e.getMessage))

  def deleteUserHandler(userId: UserId): Password => ZIO[AppEnv, Error, Unit] =
    password =>
      executeIfPasswordCorrect(
        Credentials(userId, password),
        userId,
        _deleteUserHandler
      )
