package app.domain.credentials

import app.domain.{UserId, Password}

case class PostAuthCredentials(
                               userId: UserId,
                               password: Password
                             )
given Conversion[PostAuthCredentials, Credentials] = c => Credentials(c.userId, c.password)
