package app.domain

import app.domain.Username

case class User(
                 username: Username,
                 passwordHash: String
               )

