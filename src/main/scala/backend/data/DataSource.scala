package backend.data

import com.augustnagro.magnum.{DbCon, Transactor}
import com.zaxxer.hikari.HikariDataSource

import javax.sql.DataSource

val dataSource: DataSource = {
  val hikari = new HikariDataSource()
  hikari.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb")
  hikari.setUsername("app_user")
  hikari.setPassword("password")
  hikari.setDriverClassName("org.postgresql.Driver")
  hikari
}
