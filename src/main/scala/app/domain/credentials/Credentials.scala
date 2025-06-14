package app.domain.credentials

import app.domain.{UserIdentifier, Password}

case class Credentials(
                        identifier: UserIdentifier, 
                        password: Password
                      ) 
