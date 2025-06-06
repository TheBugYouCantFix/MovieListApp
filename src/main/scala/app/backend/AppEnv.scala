package app.backend

import data.repositories.*
import auth.jwt.JwtService

type AppEnv = MovieRepo & UserRepo & JwtService